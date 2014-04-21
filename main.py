#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import da
from datetime import *
import json 
import cron

cust_id = 1  #tmp

urls = (
    '/list','List',
    '/datelist','DateList',    
    '/new','New',  
    '/details','Details',
    '/done', 'Done',  
    '/segment', 'Segment',
    '/words', 'Words',  
    '/wordlist','WordList',
    '/', 'Index',
)

render = web.template.render('templates/',base='layout') 

class CJsonEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, date):
            return obj.strftime('%Y-%m-%d')
        else:
            return json.JSONEncoder.default(self, obj)

class Index:
    def GET(self):
        render = web.template.frender('templates/index2.html')
        return render()
        # return render.index()

class List:
    def GET(self):
        i = web.input(page=1,size=50)
        rows = da.subject.load_page((int(i.page) - 1) * int(i.size),int(i.size))
        r = {'code':1,'list':rows}
        return json.dumps(r,cls=CJsonEncoder)

class DateList:
    def GET(self):
        i = web.input(date=datetime.now().strftime('%Y-%m-%d'))
        rows = da.subject.load_by_date(cust_id,i.date)
        #date format 应该放在js端处理 ?
        date = {'shortDate':i.date,'queryDate':i.date, 'strDate':i.date,'dayOfWeek':''}
        r = {'code':1,'list':rows,'date':date,'count':len(rows)}
        return json.dumps(r)

class New:
    def POST(self):
        i = web.input(content='')
        content = web.websafe(i.content)
        pk_id = da.subject.insert(cust_id,content)
        task = da.subject.load_by_id(pk_id)
        cron.update_term_count(task) #remove to eda?
        r = {"code":1,"data":task}
        return json.dumps(r,cls=CJsonEncoder) 

class Details:
    def GET(self):
        i = web.input(pk_id=0)
        detail = da.subject.load_by_id(i.pk_id)
        r = {"code":1,"data":detail}
        return json.dumps(r,cls=CJsonEncoder) 
    def POST(self):
        i = web.input(pk_id=0,subject='',body='')
        da.subject.update(i.pk_id,subject="",body=i.body)
        cron.update_term_count_by_id(i.pk_id) #remove to eda?
        return  

import cron
class Done:
    def GET(self):
        i = web.input(id=0)
        detail = da.subject.load_by_id(i.id)
        x = cron.segment(detail.body)
        return x

    def POST(self):
        i = web.input(pk_id=0,checked='true')
        task_status = 1 if i.checked=='true' else 0
        da.subject.update(i.pk_id,task_status=task_status) 
        r = {"code":1,"data":True}
        return json.dumps(r) 

class Segment:
    def POST(self):  
    # curl -d "context=是由新浪爱问提供的分词服务，是扩展服务。 该服务分词准确率高,而且可以返回给每个词的词性，详细使用方法请看API文档" "http://ftodo.sinaapp.com/segment"
        i = web.input(context='',word_tag='1')
        if i.context:         
            x = cron.segment(i.context,i.word_tag)
            return x
        else:
            return ""

class Words:
    def GET(self):
        words = da.termdoc.load_best_terms()
        return render.words(words)

class WordList:
    def GET(self):
        i = web.input(term='')
        term_id = da.termdoc.load_term_id(i.term.replace('#',''))   
        doc_ids = da.termdoc.load_doc_ids(term_id)
        subjects = da.subject.load_by_ids(doc_ids)        
        r = {"code":1,"data":subjects}
        return json.dumps(r) 

app = web.application(urls, globals())
if __name__ == "__main__":
    # da.subject.load_page(1,10)
    app.run()