import json
import urllib.request, urllib.parse
import os


def count_note(f):
    countnote = 0
    for i in f:
        if i == '#':
            countnote += 1
    return str(countnote) + ' '


def count_for(f):
    countfor = 0
    for i in f:
        if i == 'for':
            countfor += 1
    return str(countfor) + ' '


def count_if(f):
    countif = 0
    countelif = 0
    countelse = 0
    for i in f:
        if i == 'if':
            countif += 1
        if i == 'elif':
            countelif += 1
        if i == 'else:':
            countelse += 1
    return str(countif) + ' ' + str(countelif) + ' ' + str(countelse) + ' '


def count_while(f):
    countwhile = 0
    for i in f:
        if i == 'while':
            countwhile += 1
    return str(countwhile) + ' '


def check_others(f):
    checkres = 0
    for i in f:
        if i == 'Node' or 'node':
            checkres = 1
    return str(checkres) + ' '


filename = '../samplecode/result1584197128692/main.py'  # 给定文件路径
lines = ''  # 用于将存储行的变量提前声明为string格式，避免编译器自动声明时可能由于第一行的特殊情况造成的数据类型错误
store = []
with open(filename, 'r') as file_to_read:  # 打开文件，将其值赋予file_to_read
    while True:
        lines = file_to_read.readline()  # 整行读取数据
        if not lines:  # 若该行为空
            break
        else:
            this_lines = lines.split()  # 根据空格对字符串进行切割，由于切割后的数据类型有所改变(str-array)建议新建变量进行存储
            for this_line in this_lines:  # 遍历数组并输出
                store.append(this_line)

# print(store)
# print(count_note(store))
# print(count_for(store))
# print(count_if(store))
# print(count_while(store))

f = open('../result/user1.txt', mode='a+')  # 打开文件，若文件不存在系统自动创建。 #参数name 文件名，mode 模式。
# w 只能操作写入  r 只能读取   a 向文件追加
# w+ 可读可写   r+可读可写    a+可读可追加
# wb+写入进制数据
# w模式打开文件，如果文件中有数据，再次写入内容，会把原来的覆盖掉
f.write(count_note(store))  # write 写入
f.write(count_for(store))
f.write(count_if(store))
f.write(count_while(store))
f.write(check_others(store) + '\n', )
# writelines()函数 会将列表中的字符串写入文件中，但不会自动换行，如果需要换行，手动添加换行
# #参数 必须是一个只存放字符串的列表
f.close()  # 关闭文件
