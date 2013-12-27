#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import platform
import logging
import datetime

dbw = web.database(dbn='mysql', host='127.0.0.1', db='planc', user='root', pw='root')
dbr = web.database(dbn='mysql', host='127.0.0.1', db='planc', user='root', pw='root')

const_root_local = 'D:\\gaotp\\stocks' if platform.system() == 'Windows' else '/Users/gaotianpu/Documents/stocks'

const_log_level = logging.DEBUG
def init_log(log_file):
    logger = logging.getLogger()
    fname = '%s/log/%s_%s.log'%(const_root_local,log_file,datetime.datetime.now().strftime('%Y%m%d'))
    hdlr = logging.FileHandler(fname)
    formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
    hdlr.setFormatter(formatter)
    logger.addHandler(hdlr)

    logger.setLevel(logging.NOTSET)
    return logger

if __name__ == '__main__':
    logging=init_log('test')
    logging.info('注册')

#DEBUG  Detailed information, typically of interest only when diagnosing problems.
#INFO    Confirmation that things are working as expected.
#WARNING An indication that something unexpected happened, or indicative of some problem in the near future (e.g. ‘disk space low’). The software is still working as expected.
#ERROR   Due to a more serious problem, the software has not been able to perform some function.
#CRITICAL    A serious error, indicating that the program itself may be unable to continue running.

