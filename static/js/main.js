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
		$.getJSON('/datelist', {'date':date,'r':Math.random()}, function(data){
			$("#container").append(juicer($("#tpl_date_tasklist").html(), data));

			// var divEle = $("#div_"+date);			 
			// if(divEle === undefined){				 
			// 	$("#div_"+date).html(juicer($("#tpl_date_tasklist").html(), data));
			// }else{				
				
			// }
		});

		 
		
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
			$("#list_"+today).append(juicer($("#tpl_newtask_form").html(), $.parseJSON(result)));

			$("html,body").animate({
            	scrollTop:$("#navPost").offset().top - 5
            },300);

			Index.last_content = content;
			$('#txtContent').val('');
		})		 
		return false; //禁止提交后页面刷新
	});

	$("#txtContent").keydown(function(){
		var content = $('#txtContent').val().trim(); 
		if(content!=''){
			$('#newPostForm').removeClass('alert-danger');	
			$('#btnSubmit').removeClass('btn-danger').addClass('btn-default');	
		}

	}); 

});