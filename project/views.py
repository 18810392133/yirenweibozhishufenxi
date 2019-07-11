from django.http import HttpResponse, JsonResponse
from django.shortcuts import render
from .models import star_topic,fan_phone,star_pinglun
# 创建数据库连接
import MySQLdb

# Create your views here.
def index(request):
    return render(request,'cxk/index.html')
def yingxianglipaiming(request):
    return render(request,"cxk/影响力排行.html")
def gongtongguanzhu(request):
    return render(request,"cxk/共同关注.html")
def xiaofaxian(request):
    return render(request,"cxk/小发现.html")
def shijianduan(request):
    return render(request,"cxk/时间段.html")
def yonghuhuaxiang(request):
    return render(request,"cxk/用户画像.html")
def yonghuhuati(request):
    return render(request,"cxk/用户话题.html")
def fensiqinggan(request):
    getHuaticiyun()
    getPingLunCiYun()
    return render(request,"cxk/粉丝情感.html")
def load_phone_data(request):
    fan_phones_count = fan_phone.objects.all().count()
    fan_phones = fan_phone.objects.all()
    name_dict = []
    for i in range(fan_phones_count):
        temp = (fan_phones[i].phone_name,fan_phones[i].tote)
        name_dict.append(temp)
    return JsonResponse(dict(name_dict))





def getHuaticiyun():
    keyword_count = star_topic.objects.all().count()
    file = open ('project/static/js_myself/data.js','w+',encoding='utf-8')
    file.write("var data = {")
    all_items = star_topic.objects.all()
    for i in range(keyword_count):
        file.write('"'+all_items[i].keyword+'"'+':'+str(all_items[i].tote)+',\n')
    file.write("};")
    file.close()


def getPingLunCiYun():
    keyword_count = star_pinglun.objects.all().filter(tote__gt=48).count()
    file = open('project/static/js_myself/pinglun_data.js', 'w+', encoding='utf-8')
    file.write("var pinglun_data = {")
    all_items = star_pinglun.objects.all().filter(tote__gt=48)
    for i in range(keyword_count):
        if (all_items[i].keyword == '蔡徐坤'):
            pass
        if(all_items[i].tote>40):
            file.write('"' + all_items[i].keyword + '"' + ':' + str(all_items[i].tote) + ',\n')

    file.write("};")
    file.close()
