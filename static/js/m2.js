;$(function(){
	$("#container").infinitescroll({  
            navSelector: "#load_data",     //页面分页元素--成功后自动隐藏  
            nextSelector: "#load_data a",  
            itemSelector: ".scroll " ,             
            animate: true,  
            maxPage: 10, 
            dataType: 'json',
  			appendCallback:false                                                 
        }, function(data, opts) {
        	var page = opts.state.currPage; 
        	$('#container').append(juicer($("#tpl_tasklist").html(), data)); 

		}); 


});