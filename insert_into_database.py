#!/user/bin/env python
# -*- coding:utf8 -*-
# @TIME   :7/4/2019 5:10 PM
# @Author :weige
# @File :insert_into_database.py
# 这里的代码是使用python导入mysql用

import MySQLdb

# 打开数据库连接
db = MySQLdb.connect("localhost", "root", "wjl984296155@#$", "cxk_data", charset='utf8')

# 使用cursor()方法获取操作游标
cursor = db.cursor()

# 读取文件，开始构建sql语句，插入数据
data = []
for line in open("all_datas/huaticipin.txt", "r", encoding='utf8'):  # 设置文件对象并读取每一行文件
    data.append(line)  # 将每一行文件加入到list中

for i in range(len(data)):
    temp = data[-i - 1].strip('\n').split(':', 1)
    print(temp)
    tote = temp[1]
    keyword = temp[0]
    if (int(tote) > 10):
        sql = 'insert into project_huati(keyword,tote) values ("%s","%d")' % (keyword, int(tote))
        cursor.execute(sql)

    db.commit()
# 关闭数据库连接
