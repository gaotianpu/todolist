#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import da
from datetime import *
import json 
import cron
import api
from config import dbw, smtp_host
import search


web.config.debug = False

urls = (
    '/api', api.app,     
    '/register','Register',
    '/login','Login',
    '/mind','Mind',    
    '/list','List',    
    '/datelist','DateList',    
    '/new','New',       
    '/details','Details',
    '/done', 'Done',  
    '/segment', 'Segment',
    '/words', 'Words',  
    '/wordlist','WordList',   
    '/about','About',  
    '/host',"Host",
    '/mkindex','MkIndex',
    '/sendmail',"SendMail",
    '/', 'Index',
)

render = web.template.render('templates/',base='layout') 
app = web.application(urls, globals())

store = web.session.DBStore(dbw, 'sessions')
session = web.session.Session(app, store, initializer={'user_id': 0,'nick_name':''})  

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
        if not session.user_id:
            raise web.seeother("/login")
        render = web.template.frender('templates/index2.html')
        return render() 

class Mind:
    def GET(self):
        render = web.template.frender('templates/mind.html')
        return render() 

class Host:
    def GET(self):
        web.header('Content-Type', 'application/json; charset=utf-8')         
        return  '{"code":1,"host":"ftodo.sinaapp.com"}' 

import hashlib
def encrypt_password(name,password): 
    return hashlib.sha1(name + password).hexdigest()

class Register:
    def GET(self):
        return 
    def POST(self):
        i = web.input(name='',password='',device_no='0',device_type='',os_type='',channel="",version="")
        encPass = encrypt_password(i.name,i.password)
        try:
            result = da.user.load_by_name(i.name)            
            if not result:
                # name exist, but password is not match?
                user_id = da.user.register(i.name,encPass)
                #if exist?
            elif cmp(result.password , encPass)!=0:
                return json.dumps({'code':-1,'data':"name and password is not match"})    
            else:
                user_id = result.pk_id
            token = da.user.get_access_token(user_id,i.device_no,i.device_type,i.os_type,i.channel,i.version)  
            token.name = i.name
            return json.dumps({'code':1,'data':token}) 
        except Exception,ex:
            return  json.dumps({'code':-1,'data':str(ex)}) 

class Login:
    def GET(self):
        render = web.template.frender('templates/login.html')
        return render()

    def POST(self):
        i = web.input(name='',password='',device_no='0',device_type='',os_type='',channel="",version="")
        encPass = encrypt_password(i.name,i.password)
        print 'encPass:', encPass  

        result = da.user.load_by_name(i.name)

        if not result:
            return json.dumps({'code':-1,'data':"name is not exist"})
         
        if cmp(result.password , encPass)!=0:
            return json.dumps({'code':-1,'data':"name and password is not match"})    

        if i.device_no=="0": #web            
            session.user_id = result.pk_id
            session.nick_name = result.nick_name
            web.seeother('/')
        
        token = da.user.get_access_token(result.pk_id,i.device_no,i.device_type,i.os_type,i.channel,i.version)  
        return json.dumps({'code':1,'data':token}) 
        

class List:
    def GET(self):
        i = web.input(page=1,size=50)        
        rows = da.subject.load_page(session.user_id,(int(i.page) - 1) * int(i.size),int(i.size))
        r = {'code':1,'list':rows}
        return json.dumps(r,cls=CJsonEncoder) 

class DateList:
    def GET(self):
        i = web.input(date=datetime.now().strftime('%Y-%m-%d'))
        rows = da.subject.load_by_date(session.user_id,i.date)
        #date format 应该放在js端处理 ?
        date = {'shortDate':i.date,'queryDate':i.date, 'strDate':i.date,'dayOfWeek':''}
        r = {'code':1,'list':rows,'date':date,'count':len(rows)}
        return json.dumps(r)

class New:
    def POST(self):        
        i = web.input(content='',device_no='',local_id=0,creation_date=1)
        content = web.websafe(i.content)
        pk_id = da.subject.insert(session.user_id,content)
        task = da.subject.load_by_id(pk_id)
        try:
            search.make_index(task)
        except:
            pass  
        r = {"code":1,"data":task}
        return json.dumps(r,cls=CJsonEncoder)  

class Details:
    def GET(self):
        i = web.input(pk_id=0)
        detail = da.subject.load_by_id(i.pk_id)
        try:
            search.make_index(task)
        except:
            pass  
        r = {"code":1,"data":detail}
        if detail.user_id != session.user_id:
            r = {"code":-1,"data":"not permid"}        
        return json.dumps(r,cls=CJsonEncoder) 

    def POST(self):
        i = web.input(pk_id=0,subject='',body='')
        da.subject.update(i.pk_id,session.user_id,subject="",body=i.body)
        cron.update_term_count_by_id(i.pk_id) #remove to eda?
        return  

class MkIndex:
    def GET(self):
        da.search.compute_tf_idf()
        return "ok"

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

class About:
    def GET(self):
        i = web.input(os='android')
        r1 = web.template.render('templates/android',base='layout') 
        return r1.about()

# from sae.mail import send_mail
class sendmail:
    def GET(self):
        # send_mail("gaotianpu@qq.com", "invite", "to tonight's party", smtp_host)
        return ""

if __name__ == "__main__":
    # da.subject.load_page(1,10)
    app.run()