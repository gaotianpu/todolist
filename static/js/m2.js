;var Index = {
    last_content: '',
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
            animate: true,  
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
                    console.log(i);
                }                 
            });

            //append day已存在的tasks
            for(var index in exist_days_index){
                var task_day = result1.list[index][0];
                $("#taskday_"+task_day).append( juicer($("#tpl_task_items").html(), {"items":result1.list[index][1]} ) ); 
            } 

            //remove已存在day的tasks, 剩下的全刷
            for(var i in exist_days_index){
               result1.splice(i, 1);  
            } 
            $('#container').append(juicer($("#tpl_tasklist").html(), result1));  
        });
    }); 

    //bindding
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

        $.post('/new', {'content':content}, function(result){
            var today = Date.today().toString('yyyy-MM-dd');

            //if today exist,
            var result =  $.parseJSON(result); 

            var html = juicer($("#tpl_task_item_normal").html(), result.data );
            $("#taskday_" + today+ " h3").after( html ); 
            //not exist            

            Index.last_content = content;
            $('#txtContent').val('');
        })       
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
        //dblclick  or click ?
        var li = this;
        var taskId = this.id.split('_')[1]; 

        Index.set_li_normal(li.id); 

        if($(this).find("input:text").length==0){
            $(li).attr('status','edit');

            //log.debug(taskId);
            $.getJSON('/details',{pk_id:taskId,r:Math.random()},function(data){
                log.debug(this.id);
                $(li).html(juicer($("#tpl_task_update_form").html(), data)); 
            });
        }  

    });

    $("#container").delegate('li form','submit',function(){
        $.post('/details',$( this ).serialize(),function(data){
           // Index.set_li_normal(0); 
        }); 
        return false;
    });

    $("#container").delegate('li input:button','click',function(){
       // Index.set_li_normal(0); //取消、关闭按钮        
    }); 

    //done or not done?
    $("#container").delegate('input:checkbox','change',function(){  
        var pk_id = $(this).attr("param");
        var checked = $(this).prop("checked");
        log.debug('cb,' + pk_id + checked );        
        $.post('/done',{pk_id:pk_id,checked:checked},function(data){
            // if(checked){
            //     $("#t_"+pk_id).addClass("done");
            // }else{
            //     $("#t_"+pk_id).removeClass("done");
            // }
        });      
    }); 
	   
});