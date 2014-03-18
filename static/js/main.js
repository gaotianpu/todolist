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
		var loaded_days = Index.get_loaded_days(true);
		if(loaded_days.indexOf(date)<0){
			$("#container").append('<div id="day_tasks_'+ date +'"/>');
		}

		$.getJSON('/datelist', {'date':date,'r':Math.random()}, function(data){
			//if(data.list.length==0){ return ;} 数据为空的情况下也保留占位div						 
			$("#day_tasks_" + date).html(juicer($("#tpl_date_tasklist").html(), data));

			//log.debug(date + ',' + $("#day_tasks_" + date).offset().top);
		}); 
	},

	get_loaded_days: function(dayOnly){  //获得已加载的日期
		var days=[];
		$("div[id^=day_tasks_").each(function(){
			var x = {'elementId':this.id,'offset_top':$(this).offset().top,'day':this.id.split('_')[2]  } ;
			if(dayOnly){
				days.push(this.id.split('_')[2]);
			}else{
				days.push( x  );
			}

			//var elem = 
			// days.push(this.id.split('_')[2]);
			// log.debug('cc:' + $(this).offset().top);
			
			//log.debug( x.elementId + ',' + x.offset_top + ',' + x.day );
			//$("#day_tasks_" + loaded_days[i]).offset().top			 
		});
		days.sort();
		//log.debug(days.join(','));
		return days;
	},
	
};

$(function(){
	var today = Util.format_date(new Date());
	var yestoday = Util.format_date((new Date()).addDays(-1));
	//log.debug(yestoday);

	//init
	Index.render_list(today);
	Index.render_list(yestoday);
	Index.render_list(Util.format_date((new Date()).addDays(-2)));
	Index.render_list(Util.format_date((new Date()).addDays(-3)));
	Index.render_list(Util.format_date((new Date()).addDays(-4)));

	var last_scrollTop = 0 ;
	$(window).scroll(function(e){
		var document_scrollTop = $(document).scrollTop();
		var loaded_days = Index.get_loaded_days(false);
		for(var i in loaded_days){
			if(loaded_days[i].offset_top>document_scrollTop){
				var index = i-1>0 ? i-1 : 0;
				//log.debug('day:' + loaded_days[index].day);
				break;
			}
		}
		
		// var height = 0;
		// var day = 0;
		// for(var i in loaded_days){
		// 	log.debug('document scrollTop:' + $(document).scrollTop());
		// 	// log.debug(loaded_days[i] + ':' + $("#day_tasks_" + loaded_days[i]).scrollTop());
		// 	height = height + $("#day_tasks_" + loaded_days[i]).height();
		// 	log.debug(loaded_days[i] + ':' + $("#day_tasks_" + loaded_days[i]).offset().top);
			
		// }
		// log.debug('height:' + height);

		// if( $(document).scrollTop() >  last_scrollTop  ){
		// 	log.debug("down," + $(document).scrollTop()  );
		// }else{
		// 	log.debug("up," + $(document).scrollTop() );
		// }
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