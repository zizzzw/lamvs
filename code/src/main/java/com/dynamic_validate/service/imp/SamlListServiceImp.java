package com.dynamic_validate.service.imp;

import com.dynamic_validate.dao.SamlListDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.entity.SamlList;
import com.dynamic_validate.service.GenerateListService;
import com.dynamic_validate.service.SamlListService;
import com.dynamic_validate.service.SamlTypeService;
import com.dynamic_validate.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SamlListServiceImp implements SamlListService {
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private SamlListDao samlListDao;
    @Autowired
    private SamlTypeService samlTypeService;
    @Autowired
    private GenerateListService generateListService;

    /**
     * （1-2）saml_list表
     * 判断标准：
     *      每个list所依赖的type和list存在。
     *
     * 按id顺序扫描整个saml_list表。
     *
     * step1、member_classify=0，type类型的list：
     *      members的每个成员在saml_type的id中可查到
     *          具体Type是否正确，肯定要这个Type自己负责，（1）中负责。
     * step2、member_classify=1，list类型的list：
     *      members的每个成员在saml_list的id中可查到
     *          考虑到规则的分子或分母中，可能会出现多个表达式，就是list嵌套list，也就是list的元素是list。
     *          具体list是否正确，同样要这个list自己负责，（2）中递归负责。
     */
    @Override
    public void listCheck() {
        samlListDao.updateAllValid2();// 先全部置为2
        samlListDao.updateValid0(); // 把没法验证的置为0
        generateListService.listDistinct(); // 先去重，再验证。
        List<SamlList> all = samlListDao.findAllValid2(); // 能验的，就是全部了
        for (SamlList list : all) {
            int id = list.getId();
            boolean flag = checkOneList(list);
            int vali = flag ? 1 : 0;
            samlListDao.updateValid(id, vali);
        }
    }

    @Override
    public boolean checkOneList(SamlList samlList) {
        boolean flag = true;
        int id = samlList.getId();
        System.out.println("\nID为 " + id + " 的list，exp：" + samlList.getExp() + "，开始检测......");

        int[] member_type_id = StrUtil.splitInt(samlList.getMemberType());
        int[] member_list_id = StrUtil.splitInt(samlList.getMemberList());

        if (member_type_id == null && member_list_id == null) {
            flag = false;
        } else {
            boolean mt_flag = true;
            if (member_type_id != null) {
                for (int i = 0; i < member_type_id.length; i++) {
                    int id_i = member_type_id[i];
                    boolean i_flag;
                    if (!samlTypeDao.existsById(id_i)) {
                        System.out.println("ID为" + id + "的list，其ID为" + id_i + "的成员Type不存在！");
                        i_flag = false;
                    } else {
                        System.out.println("ID为" + id + "的list，其ID为" + id_i + "的成员Type存在");
                        i_flag = true;
                    }
                    mt_flag = i_flag && mt_flag;
                }
            }

            boolean ml_flag = true;
            if (member_list_id != null) {
                for (int i = 0; i < member_list_id.length; i++) {
                    int id_i = member_list_id[i];
                    boolean i_flag;
                    if (!samlListDao.existsById(id_i)) {
                        i_flag = false;
                        System.out.println("ID为" + id + "的list，其ID为" + id_i + "的成员List不存在！");
                    } else {
                        System.out.println("ID为" + id + "的list，其ID为" + id_i + "的成员List存在");
                        i_flag = true;
                    }

                    ml_flag = ml_flag && i_flag;
                }
            }

            flag = mt_flag && ml_flag;
        }

        System.out.println("ID为 " + id + " 的list，remark：" + samlList.getExp() + "，结束检测\n");
        return flag;
    }

    /**
     * 参数表中无函数时，如何解析
     *
     * 1、根据exp唯一判断是否存在
     * 2、如果有，更新这一条，返回当前list_id。
     *    如果没有，插入members，获取并返回当前的list_id。
     */
    @Override
    public int insertUpdateListId(String members_str, int listCompound) {
        if (members_str == null || members_str.length() == 0) {
            return 0;
        }
        String members_int = "";
        if (members_str.matches("[\\d, ]+")) { // 全是数字，结构体解析完后就已经是了。
            members_int = members_str;
        } else { // 函数解析的情况
            String[] tmps = members_str.split(",");//如果没有分隔符就是字符串本身，字符串数组长为1
            for (int j = 0; j < tmps.length; j++) {
                if (tmps[j].matches("^\\d+$")) { // 如果是数字，也就是函数参数的ID
                    members_int = members_int + tmps[j] + ",";
                } else {
                    //这里还是需要一个typeExist()，获取id，若不存在返回0。
                    int tmp_members_id = samlTypeService.typeExist(tmps[j]);
                    members_int = members_int + tmp_members_id + ",";
                }
            }
        }
        if(members_int.endsWith(",")) {
            members_int = members_int.substring(0, members_int.length() - 1); //去掉最后的逗号吧
        }
        int list_id;
        List<SamlList> list = samlListDao.findSamlListByExp(members_str);
        if (list == null || list.size() == 0) {
            SamlList curList = new SamlList(members_int, listCompound, members_str);
            list_id = samlListDao.save(curList).getId();
        } else {
            list_id = list.get(0).getId();
            samlListDao.updateById(list_id, members_int, listCompound, members_str);
        }

        //samlListDao.insertIgnore(members_int, memberClassify, listCompound, members_str);
        //list_id = samlListDao.findSamlListByMembersAndMemberClassify(members_int, memberClassify).get(0).getId();

        return list_id;
    }

    @Override
    public int insertUpdateListId(String members_str, String members_int, int listCompound) {
        if (members_str == null || members_str.length() == 0) {
            return 0;
        }
        int list_id;
        List<SamlList> list = samlListDao.findSamlListByExp(members_str);
        if (list == null || list.size() == 0) {
            SamlList curList = new SamlList(members_int, listCompound, members_str);
            list_id = samlListDao.save(curList).getId();
        } else {
            list_id = list.get(0).getId();
            samlListDao.updateById(list_id, members_int, listCompound, members_str);
        }
        return list_id;
    }

}
