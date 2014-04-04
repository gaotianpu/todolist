#!/usr/bin/env python
# -*- coding: utf-8 -*-
import re
import urllib
import browser
import math
import time

import sys
reload(sys)
sys.setdefaultencoding('utf-8')

import da


regex = re.compile("resultbarnum:([^-]*)")
def get_doc_count(kword):
	html = browser.download("http://www.sogou.com/web?%s" % (urllib.urlencode({'query': kword}) ) )
	l = regex.findall(html)
	l = [int(n.replace(',','')) for n in l]
	return l[0] if l else 0

def run():
	terms = da.termdoc.load_has_sogou_terms()
	doc_count = max([t.sogou_ix_count for t in terms])
	
	terms = da.termdoc.load_sogou_terms()
	for t in terms:
		term_count = get_doc_count(t.term)
		idf = math.log(float(doc_count+1) / (term_count + 1))  
		da.termdoc.update_sogou_idf(t.term,term_count,idf)
		time.sleep(50) #secon

def fix():
	terms = da.termdoc.load_has_sogou_terms()
	max_doc_count = max([t.sogou_ix_count for t in terms])
	print max_doc_count
	for t in terms:
		idf = math.log(float(max_doc_count+1) / (t.sogou_ix_count + 1))
		tf_idf = t.count * idf
		da.termdoc.update_a(t.term,sogou_idf=idf,sogou_tf_idf=tf_idf)

if __name__ == "__main__":
	run()
	fix()
	

