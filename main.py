#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import da
from datetime import *
import simplejson as json

urls = (
    '/datelist','DateList',    
    '/new','New',    
    '/', 'Index',
)

render = web.template.render('templates/',base='layout')

class Index:
    def GET(self):
        return render.index()

class DateList:
    def GET(self):
        i = web.input(date=datetime.now().strftime('%Y-%m-%d'))
        rows = da.subject.load_by_date(i.date)
        r = {'code':1,'list':rows}
        return json.dumps(r)

class New:
    def POST(self):
        i = web.input(content='')
        da.subject.insert(1,i.content,i.content)
        return '{"code":1}'  

app = web.application(urls, globals())
if __name__ == "__main__":
    app.run()