package com.dynamic_validate;

import com.dynamic_validate.dao.DemandInvokeDao;
import com.dynamic_validate.dao.SamlTypeDao;
import com.dynamic_validate.data.Data;
import com.dynamic_validate.entity.Project;
import com.dynamic_validate.entity.SamlType;
import com.dynamic_validate.service.DataImportService;
import com.dynamic_validate.service.GenerateListService;
import com.dynamic_validate.service.RuleService;
import com.dynamic_validate.service.SamlTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@SpringBootTest
class DynamicValidateApplicationTests {
    @Value("${lamvs.project}")
    int pro;
    @Value("${import.design.fromLastWrong}")
    private boolean fromLastWrongImport; // 从上次出错位置继续导入设计

    // @Resource 自动注入
    @Resource
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SamlTypeDao samlTypeDao;
    @Autowired
    private DataImportService dataImportService;
    @Autowired
    private DemandInvokeDao demandInvokeDao;
    @Autowired
    private SamlTypeService samlTypeService;
    @Autowired
    private GenerateListService generateListService;
    @Autowired
    private RuleService ruleService;

    @Test
    void testTypeService() {

        //ruleService.TR21()

        //String s = "union { struct list_head d_lru; wait_queue_head_t *d_wait; };";

        //SamlType inode = samlTypeDao.findSamlTypeById(4611);
        //generateListService.generStructListByExp(inode);

        //String s = "struct bin_attribute {struct attribute attr; size_t   size; void   *private; ssize_t (*read)(struct file *, struct kobject *, struct bin_attribute *,char *, loff_t, size_t); ssize_t (*write)(struct file *, struct kobject *, struct bin_attribute *,char *, loff_t, size_t); int (*mmap)(struct file *, struct kobject *, struct bin_attribute *attr,struct vm_area_struct *vma); };";
        //s = "struct pguestfs_dir_entry {atomic_t in_use; struct kref ref; struct list_head pgde_openers; spinlock_t pgde_unload_lock; struct completion *pgde_unload_completion; const struct inode_operations *pguestfs_iops; const struct file_operations *pguestfs_fops; union { const struct seq_operations *seq_ops; int (*single_show)(struct seq_file *, void *); };pguestfs_write_t write; void *data; unsigned int state_size; unsigned int low_ino; nlink_t nlink; kuid_t uid; kgid_t gid; loff_t size; struct pguestfs_dir_entry *parent; struct rb_root subdir; struct rb_node subdir_node; char *name; umode_t mode; u8 namelen; char inline_name[]; };";
        //s = "union{ union a{};union{};} ab;";
        //String reg = "union\\s*\\{.*\\}.*";
        //Matcher m = Pattern.compile(reg).matcher(s);
        //while (m.find()) {
        //    String in_exp = m.group().trim();
        //    System.out.println(in_exp + "==================in_exp");
        //}


        //SamlType s = samlTypeDao.findSamlTypeById(2902);
        //generateListService.generStructListByExp(s);

        //generateTypeService.generOneFileByPath("fs/ntfs/dir.h", 6);

        //SamlType t = samlTypeDao.findSamlTypeById(194);
        //generateListService.generFuncListByExp(t);


        //String s = "struct bin_attribute {struct attribute attr; size_t   size; void   *private; ssize_t (*read)(struct file *, struct kobject *, struct bin_attribute *,char *, loff_t, size_t); ssize_t (*write)(struct file *, struct kobject *, struct bin_attribute *,char *, loff_t, size_t); int (*mmap)(struct file *, struct kobject *, struct bin_attribute *attr,struct vm_area_struct *vma); };";
        //s = "static inline kernel_cap_t cap_combine( kernel_cap_t a,  kernel_cap_t b)";
        //String[] ss = generateListService.parseFuncByExp(s);


        //SamlType inode = samlTypeDao.findSamlTypeById(12020);
        //generateListService.generStructListByExp(inode);

        //List<SamlType> list = samlTypeDao.findByName("xxoo");
        //System.out.println(list.isEmpty());
        //SamlType t = samlTypeDao.findSamlTypeById(121622);
        //System.out.println(t.getList1Exp() == null);

        //SamlType t = samlTypeDao.findSamlTypeById(2409);
        //String s = "struct open_flags {int open_flag; umode_t mode; int acc_mode; int intent; int lookup_flags; };";
        //String[] struct_split_exp = generateListService.parseStructByExp(t);
        //if (struct_split_exp == null) {
        //    System.out.println(t.getId() + ":" + t.getName() + "，exp不符合规范！");
        //    return;
        //}
        //System.out.println("===========" + struct_split_exp[0] + "==========");
        //System.out.println("===========" + struct_split_exp[1] + "==========");
        //System.out.println("===========" + struct_split_exp[2] + "==========");

        //System.out.println(pro);
        //System.out.println(fromLastWrongImport);

        //List<SamlType> lis = samlTypeDao.findByLevel(18);
        //System.out.println(lis.isEmpty());

        //List<Integer> list1 = new ArrayList<>();
        //list1.add(1);
        //list1.add(3);
        //List<Integer> list2 = new ArrayList<>();
        //list2.addAll(list1);
        //list2.add(4);
        //list2.add(4);
        //List<Integer> list = samlTypeService.mergeNoRepeat(list1, list2);
        //System.out.println(list);
    }

    @Test
    void testReg() {

        List<SamlType> list = samlTypeDao.findWrongExp();
        for (SamlType t : list) {
            generateListService.generStructListByExp(t);
        }

        //SamlType t = samlTypeDao.findSamlTypeById(10410); //7630 11166 12709 20059 58754 784 300 767 7199 7630 12709 10410
        //generateListService.generStructListByExp(t);

        //String s = "struct lockref {union { struct { spinlock_t lock; int count; };};};";
        //s = generateListService.parseInnerStruct(s);
        //System.out.println(s);
        //
        //System.out.println(StrUtil.funcIgnoreWithStar("union").trim());
        //
        //String body = "umode_t   i_mode; unsigned short  i_opflags; kuid_t   i_uid; kgid_t   i_gid; unsigned int  i_flags; const struct inode_operations *i_op; struct super_block *i_sb; struct address_space *i_mapping; unsigned long  i_ino; union { const unsigned int i_nlink; unsigned int __i_nlink; };dev_t   i_rdev; loff_t   i_size; struct timespec64 i_atime; struct timespec64 i_mtime; struct timespec64 i_ctime; spinlock_t  i_lock; unsigned short          i_bytes; unsigned int  i_blkbits; enum rw_hint  i_write_hint; blkcnt_t  i_blocks; unsigned long  i_state; struct rw_semaphore i_rwsem; unsigned long  dirtied_when; unsigned long  dirtied_time_when; struct hlist_node i_hash; struct list_head i_io_list; struct list_head i_lru; struct list_head i_sb_list; struct list_head i_wb_list; union {struct hlist_head i_dentry; struct rcu_head  i_rcu; };atomic64_t  i_version; atomic_t  i_count; atomic_t  i_dio_count; atomic_t  i_writecount; const struct file_operations *i_fop; struct file_lock_context *i_flctx; struct address_space i_data; struct list_head i_devices; union {struct pipe_inode_info *i_pipe; struct block_device *i_bdev; struct cdev  *i_cdev; char   *i_link; unsigned  i_dir_seq; };__u32   i_generation; void   *i_private;";
        //body = "const struct file_operations *pguestfs_fops; union { const struct seq_operations *seq_ops; int (*single_show)(struct seq_file *, void *); };";
        //Matcher m = Pattern.compile(Data.unionEnumStructReg).matcher(body);
        //while (m.find()) {
        //    String in_exp = m.group().trim();
        //    System.out.println(in_exp + "==================in_exp");
        //    String[] strs = generateListService.splitNameBody(in_exp);
        //    String in_name = strs[0];
        //    String in_body = strs[1];
        //
        //    System.out.println("00000000" + in_name + "000000" + in_body);
        //}

        //String s = "short   i_mode; unsigned short  i_opflags; kuid_t   i_uid; kgid_t   i_gid; unsigned int  i_flags; struct posix_acl *i_acl; struct posix_acl *i_default_acl; const struct inode_operations *i_op; struct super_block *i_sb; struct address_space *i_mapping; void   *i_security; unsigned long  i_ino; union { const unsigned int i_nlink; unsigned int __i_nlink; };dev_t   i_rdev; long   i_size; struct timespec64 i_atime; struct timespec64 i_mtime; struct timespec64 i_ctime; spinlock_t  i_lock; unsigned short          i_bytes; unsigned int  i_blkbits; enum rw_hint  i_write_hint; blkcnt_t  i_blocks; seqcount_t  i_size_seqcount; unsigned long  i_state; struct rw_semaphore i_rwsem; unsigned long  dirtied_when; unsigned long  dirtied_time_when; struct hlist_node i_hash; struct list_head i_io_list; struct bdi_writeback *i_wb; int   i_wb_frn_winner; u16   i_wb_frn_avg_time; u16   i_wb_frn_history; struct list_head i_lru; struct list_head i_sb_list; struct list_head i_wb_list; union { struct hlist_head i_dentry; struct rcu_head  i_rcu; };atomic64_t  i_version; atomic_t  i_count; atomic_t  i_dio_count; atomic_t  i_writecount; atomic_t  i_readcount; const struct file_operations *i_fop; struct file_lock_context *i_flctx; struct address_space i_data; struct list_head i_devices; union { struct pipe_inode_info *i_pipe; struct block_device *i_bdev; struct cdev  *i_cdev; char   *i_link; unsigned  i_dir_seq; };__u32   i_generation; __u32   i_fsnotify_mask; struct fsnotify_mark_connector __rcu *i_fsnotify_marks; struct fscrypt_info *i_crypt_info; void   *i_private;";
        //s = "pguest_mm_tunnel_bus * pguest_mm_tunnel_bus_get(struct pguest_mm_tunnel_bus *bus) ";
        //s = "struct tree_descr { const char *name; const struct file_operations *ops; int mode; }; ";
        //
        //System.out.println(s.matches(".* [_a-zA-Z0-9]+ *\\{"));
        //String name = StrUtil.splitName(s, 3);
        //System.out.println(name);
    }


    @Test
    void testFile() {
        String s = "include/linux/seq_file.h";
        String source = Data.copy_sour_guest + "\\" + s;

        String dest = Data.source_guest + "\\" + s;
        //File sf = new File(source);
        //File df = new File(dest);
        //try {
        //    Files.copy(sf.toPath(), df.toPath());
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}

        dataImportService.importFromOneFile(dest, Data.source_guest, 7);


    }


    @Test
    void testEquals() {
        //demandInvokeDao.updateFuncs(47425, 46313, 46398);

        //SamlType t = samlTypeDao.findSamlTypeById(45798);
        //System.out.println(t.getName().equals(""));
        //SamlType a = samlTypeDao.findSamlTypeById(5);
        //SamlType b = samlTypeDao.findSamlTypeById(5);
        //System.out.println(a.equals(b));
    }

    @Test
    void testExp() {
        //String expRemark = "ssize_t (*read) (struct file *, char __user *, size_t, loff_t *)";
        //String expRemark = "long do_sys_open ( int dfd, const char __user *filename,int flags, umode_t mode);";
        String expRemark = "static inline int build_open_flags(int flags, umode_t mode, struct open_flags *op)";
        //System.out.println(DataImportServiceImp.getFuncType(expRemark));
        //DataImportServiceImp.parseFunctionByExp(expRemark);
        expRemark = expRemark.replaceAll("\\bumode_t\\b", "short");
        System.out.println(expRemark);
    }

    @Test
    void contextLoads() {

    }


    @Test
    public void mySqlTest() {
        System.out.println(jdbcTemplate.getClass().getName());
        String sql = "select * from project";
        List<Project> projectList = jdbcTemplate.query(sql, new RowMapper<Project>() {
            @Override
            public Project mapRow(ResultSet resultSet, int i) throws SQLException {
                Project project = new Project();
                project.setId(resultSet.getInt("id"));
                project.setName(resultSet.getString("name"));
                project.setChineseName(resultSet.getString("chinese_name"));
                return project;
            }
        });

        System.out.println("查询成功");
        for (Project p : projectList) {
            System.out.println("【id】：" + p.getId() + "，【name】：" + p.getName() + "，【chinese_name】：" + p.getChineseName());
        }
    }
}
