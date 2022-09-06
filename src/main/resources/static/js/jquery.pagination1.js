// jquery.pagination.js v1.0.0 By LT
;(function ($, window, document, undefined) {
    //配置参数
    var defaults = {
        totalData: 0,			//数据总条数
        showData: 0,				//每页显示的条数
        pageCount: 9,			//总页数,默认为9
        current: 1,				//当前第几页
        prevCls: 'prev',		//上一页class
        nextCls: 'next',		//下一页class
        prevContent: '&laquo;',	//上一页内容
        nextContent: '&raquo;',	//下一页内容
        activeCls: 'active',		//当前页选中状态
        coping: true,			//首页和尾页
        homePage: '首页',			//首页节点内容
        endPage: '末页',				//尾页节点内容
        count: 3,				//当前页前后分页个数
        jump: true,				//跳转到指定页数
        jumpIptCls: 'jump-ipt',	//文本框内容
        jumpBtnCls: 'jump-btn',	//跳转按钮
        jumpBtn: '跳转',			//跳转按钮文本
        callback: function (currPageNmu) {
        }	//回调(参数：当前页码)
    };

    var Pagination = function (element, options) {
        //全局变量
        var opts = options,//配置
            current,//当前页
            $document = $(document),
            $obj = $(element);//容器

        if (opts.showData === 0) {
            console.log('请设置没页显示的数据条数：showData');
            return;
        }
        //获取总页数
        this.getTotalPage = function () {
            return opts.totalData && opts.showData ? Math.ceil(parseInt(opts.totalData) / opts.showData) : opts.pageCount;
        };

        //获取当前页
        this.getCurrent = function () {
            return current;
        };

        //填充数据
        this.filling = function (index) {
            var html = [];
            current = index || opts.current;//当前页码

            var pageCount = this.getTotalPage();

            if (current > pageCount) current = 1;

            if (current > 1) {//上一页
                html.push('<li><a href="javascript:;" class="' + opts.prevCls + ' num-item"><span aria-hidden="true">' + opts.prevContent + '</span></a></li>');
            } else {
                $obj.find('.' + opts.prevCls) && $obj.find('.' + opts.prevCls).remove();
            }
            if (current >= opts.count * 2 && current != 1 && pageCount != opts.count) {
                var home = opts.coping && opts.homePage ? opts.homePage : '1';
                html.push(opts.coping ? '<li><a href="javascript:;" data-page="1" class="num-item">' + home + '</a><a href="javascript:;" class="first-dot">...</a></li>' : '');
            }
            var start = current - opts.count,
                end = current + opts.count;
            ((start > 1 && current < opts.count) || current == 1) && end++;
            (current > pageCount - opts.count && current >= pageCount) && start++;
            for (; start <= end; start++) {
                if (start <= pageCount && start >= 1) {
                    if (start != current) {
                        html.push('<li><a href="javascript:;" data-page="' + start + '" class="num-item">' + start + '</a></li>');
                    } else {
                        html.push('<li class="' + opts.activeCls + '"><a href="javascript:;">' + start + '</a></li>');
                    }
                }
            }
            if (current + opts.count < pageCount && current >= 1 && pageCount > opts.count) {
                var end = opts.coping && opts.endPage ? opts.endPage : pageCount;
                html.push(opts.coping ? '<li><a href="javascript:;" class="last-dot">...</a><a href="javascript:;" class="num-item" data-page="' + pageCount + '">' + end + '</a></li>' : '');
            }
            if (current < pageCount) {//下一页
                html.push('<li><a href="javascript:;" class="' + opts.nextCls + ' num-item">' + opts.nextContent + '</a></li>');
            } else {
                $obj.find('.' + opts.nextCls) && $obj.find('.' + opts.nextCls).remove();
            }

            html.push(opts.jump ? '<br/><br/><input type="text" class="' + opts.jumpIptCls + '"><a href="javascript:;" class="' + opts.jumpBtnCls + ' num-item">' + opts.jumpBtn + '</a>' : '');
            // html.push(opts.jump ? '<input type="text" class="' + opts.jumpIptCls + '"><a href="javascript:;" class="' + opts.jumpBtnCls + ' num-item">' + opts.jumpBtn + '</a>' : '');

            $obj.empty().html(html.join(''));
        };

        //绑定事件
        this.eventBind = function () {
            var self = this;
            var pageCount = this.getTotalPage();//总页数
            $obj.off('click').on('click', '.num-item', function () {
                if ($(this).hasClass(opts.nextCls)) {
                    var index = parseInt($obj.find('.' + opts.activeCls).text()) + 1;
                } else if ($(this).hasClass(opts.prevCls)) {
                    var index = parseInt($obj.find('.' + opts.activeCls).text()) - 1;
                } else if ($(this).hasClass(opts.jumpBtnCls)) {
                    if ($obj.find('.' + opts.jumpIptCls).val() !== '') {
                        var index = parseInt($obj.find('.' + opts.jumpIptCls).val());
                    } else {
                        return;
                    }
                } else {
                    var index = parseInt($(this).data('page'));
                }
                self.filling(index);
                typeof opts.callback === 'function' && opts.callback(index);
            });
            $obj.off('input propertychange').on('input propertychange', '.' + opts.jumpIptCls, function () {
                var val = $(this).val();
                var reg = /[^\d]/g;
                if (reg.test(val)) {
                    $(this).val(val.replace(reg, ''));
                }
                (parseInt(val) > pageCount) && $(this).val(pageCount);
                if (parseInt(val) === 0) {//最小值为1
                    $(this).val(1);
                }
            });
            $document.off('keydown').keydown(function (e) {
                if (e.keyCode == 13 && $obj.find('.' + opts.jumpIptCls).val()) {
                    var index = parseInt($obj.find('.' + opts.jumpIptCls).val());
                    self.filling(index);
                    typeof opts.callback === 'function' && opts.callback(index);
                }
            });
        };

        //初始化
        this.init = function () {
            this.filling(opts.current);
            this.eventBind();
        };
        this.init();
    };

    $.fn.pagination = function (parameter, callback) {
        if (typeof parameter == 'function') {
            callback = parameter;
            parameter = {};
        } else {
            parameter = parameter || {};
            callback = callback || function () {
            };
        }
        var options = $.extend({}, defaults, parameter);
        return this.each(function () {
            var pagination = new Pagination(this, options);
            callback(pagination);
        });
    };
    return $;
})(jQuery, window, document)
