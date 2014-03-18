var log={
	debug:function(str){
		console.log(str);
	},
}

Date.prototype.addDays = function(days){
    return new Date(this.getTime() + 24*60*60*1000*days);
};

var Util = {
	format_date:function(date){
		var month = date.getMonth() + 1;
		month = month<10 ? "0"+month : month;
        return date.getFullYear() + "" + month + '' + date.getDate();
	},
};

var Index = {
	last_content: '',
	show_dates:[],

	render_list:function(date){
		var loaded_days = Index.get_loaded_days();
		if(loaded_days.indexOf(date)<0){
			$("#container").append('<div id="day_tasks_'+ date +'"/>');
		}

		$.getJSON('/datelist', {'date':date,'r':Math.random()}, function(data){
			//if(data.list.length==0){ return ;} 数据为空的情况下也保留占位div						 
			$("#day_tasks_" + date).html(juicer($("#tpl_date_tasklist").html(), data));
		}); 
	},

	get_loaded_days: function(){  //获得已加载的日期
		var days=[];
		$("div[id^=day_tasks_").each(function(){
			days.push(this.id.split('_')[2]);			 
		});
		days.sort();
		//log.debug(days.join(','));
		return days;
	},
	
};

$(function(){
	var today = Util.format_date(new Date());
	var yestoday = Util.format_date((new Date()).addDays(-1));
	log.debug(yestoday);

	//init
	Index.render_list(today);
	Index.render_list(yestoday);
	Index.render_list(Util.format_date((new Date()).addDays(-2)));
	Index.render_list(Util.format_date((new Date()).addDays(-3)));
	Index.render_list(Util.format_date((new Date()).addDays(-4)));

	var last_scrollTop = 0 ;
	$(window).scroll(function(e){		
		if( $(document).scrollTop() >  last_scrollTop  ){
			log.debug("down," + $(document).scrollTop()  );
		}else{
			log.debug("up," + $(document).scrollTop() );
		}

		last_scrollTop = $(document).scrollTop();		 
	});

	//form
	$("#txtContent").keydown(function(){
		var content = $('#txtContent').val().trim(); 
		if(content!=''){
			$('#newPostForm').removeClass('alert-danger');	
			$('#btnSubmit').removeClass('btn-danger').addClass('btn-default');	
		}
	});

	//bindding
	$('#newPostForm').submit(function(){
		//alert($.now());
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
			var data = 	$.parseJSON(result);		 
			$("#list_"+today).append(juicer($("#tpl_newtask_form").html(), data)); 			 
			$("html,body").animate({				
            	scrollTop: $("#day_tasks_" + today).height() - $(window).height() + 76  //计算定位比较陌生
            },300);

			Index.last_content = content;
			$('#txtContent').val('');
		})		 
		return false; //禁止提交后页面刷新
	});  

});