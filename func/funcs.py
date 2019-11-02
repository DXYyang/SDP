import pandas as pd
import numpy as np
import pickle
from keras.preprocessing.sequence import pad_sequences
from sklearn.model_selection import train_test_split
from keras.models import load_model
from sklearn.metrics import f1_score,roc_auc_score

def LoadTraditionData(path):
    df=pd.read_csv(path,sep=",",encoding='utf-8')
    return df[["wmc","dit","noc","cbo","rfc","lcom","ca","ce",
                               "npm","lcom3","loc","dam","moa","mfa",
                               "cam","ic","cbm","amc","max_cc","avg_cc"]].values
def LoadCSV(path):
    return pd.read_csv(path,sep=',')

def LoadTraditionCSV(path):
    df=pd.read_csv(path,sep=",",encoding='utf-8')
    dflist=df['name.1'].tolist()
    dfdict={item:index for index,item in enumerate(dflist)}
    df['name.1']=df['name.1'].map(lambda x:dfdict[x])
    return df[["name.1","wmc","dit","noc","cbo","rfc","lcom","ca","ce",
                               "npm","lcom3","loc","dam","moa","mfa",
                               "cam","ic","cbm","amc","max_cc","avg_cc"]].values,dfdict

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

def savepickle(data,saveplace):
    with open(saveplace,'wb')as file:
        pickle.dump(data,file,-1)

def loadpickle(loadplace):
    with open(loadplace,'rb')as file:
        data=pickle.load(file)
    return data

def JoinSubList(list):
    new_list=[]
    for itemlist in list:
        newLine=' '.join(itemlist)
        new_list.append(newLine)
    return new_list

def LoadSaveData(path):
    with open(path,'rb') as f:
        loaded_data=pickle.load(f)
    return loaded_data

def dataload(trainPickle,trainCSV,testPickle,testCSV):
    data_dir='..\\dataset\\'
    training_list=LoadSaveData(data_dir+trainPickle)
    trainingDF_Bug=LoadCSV(data_dir+trainCSV)['bugs'].tolist()
    training_listLabel=MakeLabels(trainingDF_Bug)
    testing_list=LoadSaveData(data_dir+testPickle)
    testingDF_Bug=LoadCSV(data_dir+testCSV)['bugs'].tolist()
    testing_listLabel=MakeLabels(testingDF_Bug)
    return (training_list,training_listLabel,testing_list,testing_listLabel)

def getTrainTestValidSet(training_list,training_listLabel,testing_list,testing_listLabel,tokenizer):
    token_dir='..\\dataset\\'
    MAX_LEN=2000
    new_training_list=JoinSubList(training_list)
    new_testing_list=JoinSubList(testing_list)
    tokenizer=LoadSaveData(token_dir+tokenizer)
    tokenizer.oov_token = None
    train_sequences=tokenizer.texts_to_sequences(new_training_list)
    test_sequences=tokenizer.texts_to_sequences(new_testing_list)
    train_sequences_pad=pad_sequences(train_sequences,maxlen=MAX_LEN,padding='post')
    test_sequences_pad=pad_sequences(test_sequences,maxlen=MAX_LEN,padding='post')
    test_set_x=test_sequences_pad
    test_set_y=testing_listLabel
    train_set_x,validation_set_x,train_set_y,validation_set_y=train_test_split(train_sequences_pad,training_listLabel,
                                                                           test_size=0.3,random_state=42)
    print(train_set_x.shape,train_set_y.shape,validation_set_x.shape,validation_set_y.shape)
    return (train_set_x,train_set_y,test_set_x,test_set_y,validation_set_x,validation_set_y,MAX_LEN)

def saveTripleValues(predict_prob,true_y,predict_y):
    predict_prob,true_y,predict_y=predict_prob.tolist(),true_y.tolist(),predict_y.tolist()
    tripleRes=list(zip(predict_prob,true_y,predict_y))
    return tripleRes

def getEvaluations(testpickle,testCsv,proTokenizer,modelName,customObject):
    data_dir='..\\dataset\\'
    model_saved_path='..\\model\\'
    token_dir='..\\dataset\\'
    MAX_LEN=2000
    Batch_Size=32
    testing_list=LoadSaveData(data_dir+testpickle)
    testingDF_Bug=LoadCSV(data_dir+testCsv)['bugs'].tolist()
    testing_listLabel=MakeLabels(testingDF_Bug)
    new_testing_list=JoinSubList(testing_list)
    tokenizer=LoadSaveData(token_dir+proTokenizer)
    tokenizer.oov_token = None
    test_sequences=tokenizer.texts_to_sequences(new_testing_list)
    test_sequences_pad=pad_sequences(test_sequences,maxlen=MAX_LEN,padding='post')
    test_set_x=test_sequences_pad
    test_set_y=testing_listLabel
    model=load_model(model_saved_path+modelName
                 ,custom_objects={'AttentionWithContext':customObject})
    print("start predicting")
    predicted=model.predict(test_set_x,batch_size=Batch_Size,verbose=2)
    test_accuracy=np.mean(np.equal(test_set_y,np.rint(predicted)))
    f1=f1_score(test_set_y,np.rint(predicted))
    auc=roc_auc_score(test_set_y,predicted)
    print(test_accuracy,f1,auc)
    return (test_set_y,predicted,np.rint(predicted))

