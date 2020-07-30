from keras.utils.data_utils import get_file
import zipfile
import os
import shutil
# 下载代码
file='data.zip'
dir='../samplecode'


#获得目录下所有文件的文件名的数组
def get_filelist(dir):
    Filelist = []
    for home, dirs, files in os.walk(dir):
        for filename in files:
            # 文件名列表，包含完整路径
            Filelist.append(os.path.join(home, filename))
            # # 文件名列表，只包含文件名
            # Filelist.append( filename)
    return Filelist

#删除dir目录下的data.zip
def delete_data():
    os.chdir(dir)
    if os.path.exists("data.zip"):
        os.remove('data.zip')


'''
函数说明 getfile：
参数:
fname: 下载后你想把这个文件保存成什么名字
origin: 下载的地址链接
cache_subdir: 模型保存在哪个文件夹下
'''
def getfile(url1):
    delete_data()
    os.chdir(dir)  # 将当前工作目录改变为要解压的目录
    try:
        #解压的文件重命名为data，所以每次下载新的代码就要删掉之前的data.zip
        path = get_file(fname=file,
                        origin=url1, cache_subdir=dir)  #
    except:
        print('Error')
        raise




# 解压当前目录的某个文件到当前目录下
def un_zip(file_name):
    """unzip zip file"""
    os.chdir(dir)
    zip_file = zipfile.ZipFile(file_name)

    # print(zip_file.namelist())
    for names in zip_file.namelist():
        # print(names)
        zip_file.extract(names)
        sub_zip_file = zipfile.ZipFile(names)
        for subnames in sub_zip_file.namelist():
            # print(subnames)
            if "answer" in subnames:
                sub_zip_file.extract(subnames)
                break
        #加入到某个文件夹中 zip_file.extract(names,file_name.split(".")[0])list():
        os.chdir("../samplecode")

        #重命名 get_filelist（paht）中的path是转移.mooctest文件下的answer.py的目的地址
        Filelist = get_filelist("../samplecode")
        str1 = "answer" + str(len(Filelist)) + ".py"
        os.rename("answer.py", str1)

        #转移.mooctest文件下的answer.py到dio文件夹中
        shutil.move("../samplecode/.mooctest\\" + "answer.py", "../samplecode")

    zip_file.close()

while(True):
    s = ""+input()
    getfile(s)
    un_zip(file)