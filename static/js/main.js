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

	render_list:function(date){
		var loaded_days = Index.get_loaded_days(true);
		if(loaded_days.indexOf(date)<0){
			$("#container").append('<div id="day_tasks_'+ date +'"/>');
		}

		$.getJSON('/datelist', {'date':date,'r':Math.random()}, function(data){
			//if(data.list.length==0){ return ;} 数据为空的情况下也保留占位div						 
			$("#day_tasks_" + date).html(juicer($("#tpl_tasklist").html(), data)); 
		}); 
	},

	set_head_nav:function(day){
		var tmp = $("#day_tasks_" + day).find("h2").html()
		$("#hNavHeader").html(tmp); 
	},

	get_loaded_days: function(dayOnly){  //获得已加载的日期
		var days=[];
		$("div[id^=day_tasks_]").each(function(){ 
			var x = {'elementId':this.id,
				'offset_top':$(this).offset().top,
				'height': $(this).height(),
				'day':this.id.split('_')[2],
				'item_length':$(this).find('li').length } ;

			if(dayOnly){
				days.push(this.id.split('_')[2]);
			}else{
				days.push( x  );
			} 
		});
		days.sort();		 
		return days;
	},

	set_li_normal:function(current_edit_id){
		//只允许一个task处于编辑状态,清理重置之前处于编辑状态的li
		//是否需要post数据？
		$("li[status=edit]").each(function(){
			if(this.id!=current_edit_id){		
				var data = {'body':$(this).find("#txt_body").val()};
				var html = juicer($("#tpl_task_normal").html(), data) ;
				$(this).html(juicer($("#tpl_task_normal").html(), data)).attr('status','normal');
			}				 
		});
	},
	
};

$(function(){
	var today = Util.format_date(new Date());
	var yestoday = Util.format_date((new Date()).addDays(-1)); 

	//init
	Index.render_list(today);
	Index.render_list(yestoday);
	Index.render_list(Util.format_date((new Date()).addDays(-2))); 

	var last_doc_scrollTop = 0 ;
	$(window).scroll(function(e){
		var loaded_days = Index.get_loaded_days(false); 
		var doc_scrollTop = $(document).scrollTop();

		// log.debug('document_scrollTop,'+ doc_scrollTop);
		// log.debug('document_height,'+ $(document).height());
		// log.debug('window_height,'+ $(window).height());
		// log.debug('bottom_range,'+ bottom_range); 
		
		var last_index = 0;
		for(var i in loaded_days){
			if(loaded_days[i].item_length==0){continue;}

			if((loaded_days[i].offset_top + loaded_days[i].height) > doc_scrollTop){
				Index.set_head_nav(loaded_days[i].day);
				break;
			}

			last_index = i;
		} 

		//滚动按天加载tasks	 
		var bottom_range = $(document).height() - $(window).height() - doc_scrollTop;
		if(doc_scrollTop>last_doc_scrollTop  && 
			( bottom_range < $(window).height()*5 || $(document).height()<$(window).height() )
			) {
			log.debug('bottom_range:' + bottom_range + ',day:' + loaded_days[loaded_days.length-1].day);			 
			var intDay = loaded_days[loaded_days.length-1].day;

			var strDay = intDay.toString()   
			log.debug('cc,' + strDay.substring(0,4) + strDay.substring(4,6) + strDay.substring(6,8) )
			var date =  new Date(strDay.substring(0,4),parseInt(strDay.substring(4,6))-1,strDay.substring(6,8));
			var odate = new Date(date - 24*60*60*1000* 1);
			log.debug('odate:' + Util.format_date(odate) +',cdate:'+ Util.format_date(date) );
			
			Index.render_list(Util.format_date(odate));
		}

		last_doc_scrollTop = doc_scrollTop;	

		
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
		log.debug('cb,' + pk_id + checked );		
		$.post('/done',{pk_id:pk_id,checked:checked},function(data){
			if(checked){
				$("#t_"+pk_id).addClass("done");
			}else{
				$("#t_"+pk_id).removeClass("done");
			}
		});		 
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
			Index.set_li_normal(0);

			var data = 	$.parseJSON(result); 
			var html = '<li id="t_'+ data.pk_id +'" class="list-group-item" status="edit">';
			html = html + juicer($("#tpl_task_update_form").html(), data);
			html = html + '</li>'; 
			$("#list_"+today).append(html); 

			$("html,body").animate({				
            	scrollTop: $("#day_tasks_" + today).height() - $(window).height() + 76  //计算定位比较陌生
            },300);

            Index.set_head_nav(today);	

			Index.last_content = content;
			$('#txtContent').val('');
		})		 
		return false; //禁止提交后页面刷新
	});  

});