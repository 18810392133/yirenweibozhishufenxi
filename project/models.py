from django.db import models

# Create your models here.
class star_topic(models.Model):
    keyword = models.CharField(max_length=40)
    tote = models.IntegerField()

class fan_phone(models.Model):
    phone_name = models.CharField(max_length=40)
    tote = models.IntegerField()

class star_pinglun(models.Model):
    keyword = models.CharField(max_length=40)
    tote = models.IntegerField()
