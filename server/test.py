#!/usr/bin/env python
# -*- coding: utf-8 -*-
import web
import da

#insert(user_id,val_sort,val_account,val_code)
pk_id = da.account_validate.insert(1,1,'13811897509','5496')
da.account_validate.update_code_status(pk_id,1)
da.account_validate.check_code(1,1,'13811897509','5496')