#!/usr/bin/env python  
#-*- coding: utf-8 -*-

import Tkinter
# 进入消息循环
import csv
top = Tkinter.Tk()
file1=open('prediction.csv','rb')
file1.readline()
reader=csv.reader(file1)
file2=open('answer.csv','rb')
file2.readline()
reader_label=csv.reader(file2)
writer_accuracy = csv.writer(open('result.csv', 'wb'))
writer_accuracy.writerow(['threshold', 'good_right','good_total','accuracy0', 'bad_right','bad_total', 'accuracy1','pre_true','total', 'accuracy'])
prediction={}
label={}
for item in reader:
    prediction[item[0]]=item[1]
for item in reader_label:
    label[item[1]] = item[2]

jmax = 1
j = 0
while j <= jmax:
    sum = sum0 = sum1 = 0
    accuracy = accuracy0 = accuracy1 = 0
    for i in prediction:
        #if sum==0:
            #sum+= 1
            #continue
        sum=sum+1
        if float(prediction[i])>j:
            label0='0'
        else:
            label0='1'
#average accuracy
        if label0==label[i]:
            accuracy+=1
#bad accuracy
        if label[i]=='1':
            sum1+=1
            if label0==label[i]:
                accuracy1+=1
#good accuracy
        if label[i]=='0':
            sum0+=1
            if label0==label[i]:
                accuracy0+=1
    total=float(accuracy)/(sum)
    good=float(accuracy1)/(sum1)
    bad=float(accuracy0)/(sum0)
    writer_accuracy.writerow([j,accuracy0,sum0,bad,accuracy1,sum1,good,accuracy,sum,total])

    j += 0.0001

    
# 下面是GUI
top.mainloop()