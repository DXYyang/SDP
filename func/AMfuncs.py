import time
import pickle
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from keras.preprocessing.sequence import pad_sequences
from keras.models import load_model
from sklearn.metrics import f1_score,roc_auc_score
def LoadSaveData(path):
    with open(path,'rb') as f:
        loaded_data=pickle.load(f)
    return loaded_data
def LoadCSV(path):
    return pd.read_csv(path,sep=',')
def LoadTraditionData(path):
    df=pd.read_csv(path,sep=",",encoding='utf-8')
    return df[["wmc","dit","noc","cbo","rfc","lcom","ca","ce",
                               "npm","lcom3","loc","dam","moa","mfa",
                               "cam","ic","cbm","amc","max_cc","avg_cc"]].values
def MakeLabels(input_arr):
    result_arr=[]
    for item in input_arr:
        in_arr=[]
        if item!=0:
            in_arr.append(1)
        else:
            in_arr.append(0)
        result_arr.append(in_arr)
    return np.asarray(result_arr)
def JoinSubList(list):
    new_list=[]
    for itemlist in list:
        newLine=' '.join(itemlist)
        new_list.append(newLine)
    return new_list

def dataload(trainPickle,trainCSV,testPickle,testCSV):
    data_dir='../dataset/'
    train_traX=LoadTraditionData(data_dir+trainCSV)
    test_traX=LoadTraditionData(data_dir+testCSV)
    training_list=LoadSaveData(data_dir+trainPickle)
    trainingDF_Bug=LoadCSV(data_dir+trainCSV)['bugs'].tolist()
    training_listLabel=MakeLabels(trainingDF_Bug)
    testing_list=LoadSaveData(data_dir+testPickle)
    testingDF_Bug=LoadCSV(data_dir+testCSV)['bugs'].tolist()
    testing_listLabel=MakeLabels(testingDF_Bug)
    return (training_list,training_listLabel,testing_list,testing_listLabel,train_traX,test_traX)

def getTrainTestValidSet(training_list,training_listLabel,testing_list,testing_listLabel,tokenizer,train_traX):
    token_dir='../dataset/'
    MAX_LEN=2000
    new_training_list=JoinSubList(training_list)
    new_testing_list=JoinSubList(testing_list)
    tokenizer=LoadSaveData(token_dir+tokenizer)
    tokenizer.oov_token = None
    word_index=tokenizer.word_index
    train_sequences=tokenizer.texts_to_sequences(new_training_list)
    test_sequences=tokenizer.texts_to_sequences(new_testing_list)
    train_sequences_pad=pad_sequences(train_sequences,maxlen=MAX_LEN,padding='post')
    test_sequences_pad=pad_sequences(test_sequences,maxlen=MAX_LEN,padding='post')
    test_set_x=test_sequences_pad
    test_set_y=testing_listLabel
    train_set_x,validation_set_x,train_set_y,validation_set_y=train_test_split(train_sequences_pad,training_listLabel,
                                                                           test_size=0.3,random_state=42)
    trainIdList=[]
    valiIdList=[]
    for item in train_set_x.tolist():
        tempID=train_sequences_pad.tolist().index(item)
        trainIdList.append(tempID)
    for item in validation_set_x.tolist():
        tempID=train_sequences_pad.tolist().index(item)
        valiIdList.append(tempID)
    train_TraditionX=train_traX[trainIdList]
    validation_TraditionX=train_traX[valiIdList]
    print(train_set_x.shape,train_set_y.shape,validation_set_x.shape,validation_set_y.shape)
    return (train_set_x,train_set_y,test_set_x,test_set_y,validation_set_x,validation_set_y,train_TraditionX,validation_TraditionX,MAX_LEN)

def saveTripleValues(predict_prob,true_y,predict_y):
    predict_prob=predict_prob.tolist()
    predict_y=predict_y.tolist()
    true_y=true_y.tolist()
    tripleRes=list(zip(predict_prob,true_y,predict_y))
    return tripleRes



def getEvaluationsCK(testpickle,testCsv,proTokenizer,modelName,customObjects):
    data_dir='../dataset/'
    model_saved_path='../model/'
    token_dir='../dataset/'
    MAX_LEN=2000
    Batch_Size=8
    testing_list=LoadSaveData(data_dir+testpickle)
    testingDF_Bug=LoadCSV(data_dir+testCsv)['bugs'].tolist()
    testing_listLabel=MakeLabels(testingDF_Bug)
    tradTest_list=LoadTraditionData(data_dir+testCsv)
    new_testing_list=JoinSubList(testing_list)
    tokenizer=LoadSaveData(token_dir+proTokenizer)
    tokenizer.oov_token = None
    word_index=tokenizer.word_index
    test_sequences=tokenizer.texts_to_sequences(new_testing_list)
    test_sequences_pad=pad_sequences(test_sequences,maxlen=MAX_LEN,padding='post')
    test_set_x=test_sequences_pad
    test_set_y=testing_listLabel
    kerasdict={'AttentionWithContext':customObjects['AttentionWithContext']}
    kerasdict.update(customObjects['SeqSelfAttention'].get_custom_objects())
    model=load_model(model_saved_path+modelName,
                     custom_objects=kerasdict)
    print("start predicting")
    predicted=model.predict([test_set_x,np.tanh(tradTest_list)],batch_size=Batch_Size,verbose=2)
    test_accuracy=np.mean(np.equal(test_set_y,np.rint(predicted)))
    f1=f1_score(test_set_y,np.rint(predicted))
    auc=roc_auc_score(test_set_y,predicted)
    print(test_accuracy,f1,auc)
    return (predicted,test_set_y,np.rint(predicted),test_accuracy,f1,auc)
