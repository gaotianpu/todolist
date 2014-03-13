#!/usr/bin/env python
# -*- coding: utf-8 -*-
import os

def run():
    project_path = os.path.split(os.path.realpath(__file__))[0]    
    os.system('mysqldump -uroot -proot -d planc > %s/planc.sql' % (project_path)) 
 
 #subjects status , 尚未开始 NotBegun 0，已开始 Doing 10，结束Done2，Block3    

if __name__ == "__main__":
    run()