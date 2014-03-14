#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web

urls = (    
    '/', 'Index',
)

render = web.template.render('templates/',base='layout')

class Index:
    def GET(self):
        return render.index()

app = web.application(urls, globals())
if __name__ == "__main__":
    app.run()