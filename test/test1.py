import json
import urllib.request, urllib.parse
import os

f = open('../sources/test_data.json', encoding='utf-8')
res = f.read()
data = json.loads(res)
#print(data)
cases = data['48117']['cases']
#print(cases)
for case in cases:
    uploads = case['upload_records']
    countupload=0
    for upload in uploads:
        countupload += 1
#    print(case["case_id"], case["case_type"], case["final_score"], countupload)
    f = open('../result/user1a.txt', mode='a+')
# 输出数据到文件
    f.write(case["case_id"]+' '+case["case_type"]+' '+str(case["final_score"])+' '+str(countupload)+ '\n')