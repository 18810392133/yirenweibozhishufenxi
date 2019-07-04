from django.db import models

# Create your models here.
class star_topic(models.Model):
    keyword = models.CharField(max_length=40)
    tote = models.IntegerField(max_length=10)
