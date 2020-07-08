import json
import urllib.request,urllib.parse
import os

f = open('sample.json', encoding='utf-8')
res = f.read()
data = json.loads(res)
print(data)
cases = data[0]['cases']
print(cases)
for case in cases:
    print(case["case_id"], case["case_type"])
    filename = urllib.parse.unquote(os.path.basename(case["case_zip"]))
    print(filename)
    urllib.request.urlretrieve(case["case_zip"], filename)