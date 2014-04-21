;$(function(){
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
	   
});