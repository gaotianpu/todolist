;var Index = {
    last_content: '',

    set_li_normal:function(current_edit_id){
        //只允许一个task处于编辑状态,清理重置之前处于编辑状态的li
        //是否需要post数据？
        $("li[status=edit]").each(function(){
            if(this.id!=current_edit_id){       
                var data = {'body':$(this).find("#txt_body").val()};
                var html = juicer('<input id="cb_${pk_id}" param="${pk_id}" type="checkbox" >${body}' , data) ;
                $(this).html(html).attr('status','normal');
            }                
        });
    },
}; 

$(function(){
    //首次加载数据
    $.getJSON("/list?page=1",function(result){
        $('#container').html(juicer($("#tpl_tasklist").html(), result)); 

        //滚动加载绑定
        $("#container").infinitescroll({  
            navSelector: "#load_data",     //页面分页元素--成功后自动隐藏  
            nextSelector: "#load_data a",  
            itemSelector: ".taskday " ,             
            animate: false,  
            maxPage: 10, 
            dataType: 'json',
            appendCallback:false                                                 
        }, function(result1, opts) {
            var page = opts.state.currPage; 

            //需要判断day是否已存在dom中, 这部分逻辑是不是放在server端更合适一些？
            var exist_days_index = [];
            $(result1.list).each(function(i){
                var task_day = result1.list[i][0];
                var taskday_dom = $("#taskday_"+task_day);
                if(taskday_dom.length>0){                     
                    exist_days_index.push(i);                     
                }                 
            });

            //append day已存在的tasks
            for(var index in exist_days_index){
                var task_day = result1.list[index][0];
                $("#taskday_"+task_day).append( juicer($("#tpl_task_items").html(), {"items":result1.list[index][1]} ) ); 
            } 

            //remove已存在day的tasks, 剩下的全刷
            for(var i in exist_days_index){
                if(!!result1){
                    result1.list.splice(i, 1);  
               }
            } 
            $('#container').append(juicer($("#tpl_tasklist").html(), result1));  
        });
    }); 
    
    $(window).scroll(function(e){
        var doc_scrollTop = $(document).scrollTop();
        // console.log('doc,'+doc_scrollTop);
        var last_ul = false;
        $("#container ul").each(function(index,element){
            // console.log($(this).attr("id") + ',' + $(this).offset().top + ',' + $(this).height() ); 
            if($(this).offset().top - 100 > doc_scrollTop){
                var ele = index==0 ? this : last_ul; 
                var show_day = $(ele).attr('id').split('_')[1];
                $("#hNavHeader").html(show_day);                 
                 return false;
            } 
            last_ul  = $(this);
        });
        
    });

    //bindding
    var is_posting=false;
    $('#newPostForm').submit(function(){        
        var content = $('#txtContent').val().trim(); 

        if(content=='' || Index.last_content==content){
            $('#newPostForm').addClass('alert-danger'); 
            $('#btnSubmit').removeClass('btn-default').addClass('btn-danger');  
            return false; 
        }

        if(Index.last_content==content){
            //?
            return false;
        }

        if(!is_posting){
            is_posting = true; 
            $.post('/new', {'content':content}, function(result){
                var today = Date.today().toString('yyyy-MM-dd');
                var result =  $.parseJSON(result); 
                
                if($("#taskday_" + today).length>0){ //if today exist,
                    var html = juicer($("#tpl_task_item_normal").html(), result.data );
                    $("#taskday_" + today + " h3").after( html );               
                }else{                
                    result.data.day = today;  
                    var html = juicer($("#tpl_today_new").html(), result );
                    $('#container').prepend(html); 
                }   

                Index.last_content = content;
                $('#txtContent').val('');

                is_posting = false;
            });
        }

        return false; //禁止提交后页面刷新
    });  

    //form validate
    $("#txtContent").keydown(function(){
        var content = $('#txtContent').val().trim(); 
        if(content!=''){
            $('#newPostForm').removeClass('alert-danger');  
            $('#btnSubmit').removeClass('btn-danger').addClass('btn-default');  
        }
    }); 

    $(document).delegate('li.list-group-item', 'dblclick', function() { 
        Index.set_li_normal(0); 

        if($(this).find("input:text").length==0){
            $(this).attr('status','edit');

            var taskId = this.id.split('_')[1]; 
            var data = {"pk_id":taskId,"body":$(this).text().trim()};
            var html = juicer($("#tpl_task_update_form").html(), data) 
            $(this).html(html);
        } 

    });

    $("#container").delegate('li form','submit',function(){
        $.post('/details',$( this ).serialize(),function(data){
           Index.set_li_normal(0); 
        }); 
        return false;
    });

    $("#container").delegate('li input:button','click',function(){         
        Index.set_li_normal(0); //取消、关闭按钮        
    }); 

    //done or not done?
    $("#container").delegate('input:checkbox','change',function(){  
        var pk_id = $(this).attr("param");
        var checked = $(this).prop("checked");
        
        $.post('/done',{pk_id:pk_id,checked:checked},function(data){
            if(checked){
                $("#li_"+pk_id).addClass("done");
            }else{
                $("#li_"+pk_id).removeClass("done");
            }
        });      
    });  
	   
});