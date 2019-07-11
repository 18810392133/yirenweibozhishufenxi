#!/user/bin/env python
# -*- coding:utf8 -*-
# @TIME   :7/4/2019 5:52 PM
# @Author :weige
# @File :urls.py
from django.conf.urls import url
from django.urls import path

from project import views

app_name = 'project'
urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^yingxianglipaiming/$', views.yingxianglipaiming, name='yingxianglipaiming'),
    url(r'^xiaofaxian/$', views.xiaofaxian, name='xiaofaxian'),
    url(r'^shijianduan/$', views.shijianduan, name='shijianduan'),
    url(r'^yonghuhuaxiang/$', views.yonghuhuaxiang, name='yonghuhuaxiang'),
    url(r'^yonghuhuati/$', views.yonghuhuati, name='yonghuhuati'),
    url(r'^fensiqinggan/$', views.fensiqinggan, name='fensiqinggan'),
    url(r'^gongtongguanzhu/$', views.gongtongguanzhu, name='gongtongguanzhu'),
    url(r'^load_phone_data/$', views.load_phone_data, name='load_phone_data'),
]
