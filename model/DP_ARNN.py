# Software Defect Prediction via Attention-Based Recurrent Neural Network
# link:https://doi.org/10.1155/2019/6230953

from func.funcs import *
from keras.models import Sequential
from keras.layers import Dense,Bidirectional,LSTM
from keras.layers import Embedding
from keras.callbacks import ModelCheckpoint,EarlyStopping
from keras.callbacks import TensorBoard,CSVLogger
from model.attention import AttentionWithContext



class BiRNNAttention:
    def __init__(self,vLen,vDimension,slen,LSTMNums,HiddenOne,HiddenTwo):
        self.vLen=vLen
        self.vDimension=vDimension
        self.slen=slen
        self.LSTMNums=LSTMNums
        self.HiddenOne=HiddenOne
        self.HiddenTwo=HiddenTwo
    def getModel(self):
        model=Sequential()
        model.add(Embedding(self.vLen,
                     self.vDimension,
                     input_length=self.slen))#layer 0:an embedding layer
        #双向LSTM
        model.add(Bidirectional(LSTM(self.LSTMNums,activation='tanh',return_sequences=True),name='LSTM'))
#         model.add(MutiAttentionLayer())
        model.add(AttentionWithContext())
        model.add(Dense(self.HiddenOne,activation='tanh'))
        model.add(Dense(self.HiddenTwo))
        model.add(Dense(1,activation='sigmoid'))
        self.model=model
    def train(self,train_X,train_y,Epochs,Batch_Size,valid_X,valid_y,model_saved_path,saved_model_name):
        log_path='..\\logs\\'
        self.model.compile(loss='binary_crossentropy',optimizer='RMSprop',metrics=['accuracy'])
        callbacks_list=[ModelCheckpoint(filepath=model_saved_path+saved_model_name+'_{epoch:02d}_{val_acc:.3f}.h5',
                    monitor='val_acc',verbose=2,save_best_only=True,period=1),
                        EarlyStopping(monitor='val_acc',patience=60,verbose=2,mode="max"),
                        TensorBoard(log_dir=log_path,batch_size=Batch_Size,write_graph=True,write_grads=True,write_images=True,
                                    embeddings_freq=0,embeddings_layer_names=None,embeddings_metadata=None),
                        CSVLogger(log_path+saved_model_name+'.log')]
        print("Starting training the model...")
        self.model.fit(train_X,train_y,epochs=Epochs,batch_size=Batch_Size,shuffle=False,
                  validation_data=(valid_X,valid_y),callbacks=callbacks_list,verbose=2)
        print("Model training completed")




if __name__=='__main__':
    projectList=[['camel-1.4.pickle','AfterDeletecamel-1.4.csv','camel-1.6.pickle','AfterDeletecamel-1.6.csv'],
                ['jedit-4.0.pickle','AfterDeletejedit-4.0.csv','jedit-4.1.pickle','AfterDeletejedit-4.1.csv'],
                ['lucene-2.0.pickle','AfterDeletelucene-2.0.csv','lucene-2.2.pickle','AfterDeletelucene-2.2.csv'],
                ['poi-2.5.pickle','AfterDeletepoi-2.5.csv','poi-3.0.pickle','AfterDeletepoi-3.0.csv'],
                ['synapse-1.1.pickle','AfterDeletesynapse-1.1.csv','synapse-1.2.pickle','AfterDeletesynapse-1.2.csv']
                ,['xalan-2.5.pickle','AfterDeletexalan-2.5.csv','xalan-2.6.pickle','AfterDeletexalan-2.6.csv'],
                ['xerces-1.2.pickle','AfterDeletexerces-1.2.csv','xerces-1.3.pickle','AfterDeletexerces-1.3.csv']]
    tokenizerlist=['camel-1.4tokenizer.pickle','jedit-4.0tokenizer.pickle','lucene-2.0tokenizer.pickle'
                   ,'poi-2.5tokenizer.pickle','synapse-1.1tokenizer.pickle',
                   'xalan-2.5tokenizer.pickle','xerces-1.2tokenizer.pickle']
    modelNamelist=['AttentionRNNcamel','AttentionRNNjedit','AttentionRNNlucene'
                   ,'AttentionRNNpoi','AttentionRNNsynapse','AttentionRNNxalan','AttentionRNNxerces']
    LSTMNums=[8,16,24,32,40,48,56,64]
    HiddenOnes=[16,16,16,16,16,16,16,16]
    HiddenTwos=[8,8,8,8,8,8,8,8]
    embedding_dimesions=[30,30,30,30,30,30,30,30]
    for i in range(3):
        training_list,training_listLabel,\
        testing_list,testing_listLabel=dataload(projectList[i][0],projectList[i][1],projectList[i][2],projectList[i][3])
        train_set_x,train_set_y,\
        test_set_x,test_set_y,\
        validation_set_x,validation_set_y,Max_Len=getTrainTestValidSet(training_list,training_listLabel,
                         testing_list,testing_listLabel,tokenizerlist[i])

        token_dir='..\\dataset\\'
        tokenizer=LoadSaveData(token_dir+tokenizerlist[i])
        tokenizer.oov_token = None
        word_index=tokenizer.word_index
        for j in range(8):
            TRNN=BiRNNAttention(len(word_index)+1,embedding_dimesions[j],Max_Len,LSTMNums[j],HiddenOnes[j],HiddenTwos[j])
            TRNN.getModel()
            TRNN.train(train_set_x,train_set_y,20,32,validation_set_x,validation_set_y,
                     '..\\model\\'
                       ,modelNamelist[i]+'para'+str(j+1))



