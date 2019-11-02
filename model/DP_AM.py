# Deep Semantic Feature Learning with Embedded Static Metrics for Software Defect Prediction
#APSEC 2019
from model.attention import AttentionWithContext,SeqSelfAttention
from keras.models import Model
from keras.layers import Dense,Input,Bidirectional,LSTM,Concatenate
from keras.layers import Embedding
from keras.callbacks import ModelCheckpoint,EarlyStopping
from keras.callbacks import TensorBoard,CSVLogger
from keras import backend as K
from func.AMfuncs import *

class Self_AttentionCK_Model:
    def __init__(self, vLen, vDimension, slen, lstm_units, attention_width, hidden_units, traLen):
        self.vLen = vLen
        self.vDimension = vDimension
        self.slen = slen
        self.lstm_units = lstm_units
        self.attention_width = attention_width
        self.hidden_units = hidden_units
        self.traLen = traLen

    def build_Model(self):
        embedding_layer = Embedding(self.vLen, self.vDimension,
                                    input_length=self.slen, name='embedding')
        sequence_Input = Input(shape=(self.slen,), dtype='int32')
        embedding_sequences = embedding_layer(sequence_Input)
        x = Bidirectional(LSTM(self.lstm_units, activation='tanh', return_sequences=True), name='lstm')(
            embedding_sequences)
        x = SeqSelfAttention(units=16, attention_width=self.attention_width, attention_activation='sigmoid',
                             name='Self_Attention')(x)
        x = AttentionWithContext(name='attention')(x)
        x = Dense(self.hidden_units, activation='tanh')(x)
        trad_Input = Input(shape=(self.traLen,), dtype='float32')
        trad_Input=K.tanh(trad_Input)
        x = Concatenate(axis=-1, name='Con')([x, trad_Input])
        pred = Dense(1, activation='sigmoid')(x)
        model = Model([sequence_Input, trad_Input], pred)
        model.compile(loss='binary_crossentropy', optimizer='RMSprop', metrics=['accuracy'])
        self.model = model

    def train_Model(self, tr_X, tr_Y, epochs, batchSize, va_X, va_Y, modelSavedPath, modelName, logPath, train_TraX,
                    valid_TraX):
        callbackList = [ModelCheckpoint(filepath=modelSavedPath + modelName + '_{epoch:02d}_{val_acc:.3f}.h5',
                                        monitor='val_acc', verbose=2, save_best_only=True, period=1),
                        EarlyStopping(monitor='val_acc', patience=60, verbose=2, mode="max"),
                        TensorBoard(log_dir=logPath, batch_size=batchSize, write_graph=True, write_grads=True,
                                    write_images=True, embeddings_freq=0, embeddings_layer_names=None,
                                    embeddings_metadata=None),
                        CSVLogger(logPath + modelName + '.log')]
        print("Starting training the Model...")
        self.model.fit([tr_X, train_TraX], tr_Y, epochs=epochs, batch_size=batchSize, shuffle=False,
                       validation_data=([va_X, valid_TraX], va_Y), callbacks=callbackList, verbose=2)
        print("Model training completed")


if __name__ == '__main__':
    projectList = [['camel-1.4.pickle', 'AfterDeletecamel-1.4.csv', 'camel-1.6.pickle', 'AfterDeletecamel-1.6.csv'],
                   ['jedit-4.0.pickle', 'AfterDeletejedit-4.0.csv', 'jedit-4.1.pickle', 'AfterDeletejedit-4.1.csv'],
                   ['lucene-2.0.pickle', 'AfterDeletelucene-2.0.csv', 'lucene-2.2.pickle', 'AfterDeletelucene-2.2.csv'],
                   ['poi-2.5.pickle', 'AfterDeletepoi-2.5.csv', 'poi-3.0.pickle', 'AfterDeletepoi-3.0.csv'],
                   ['synapse-1.1.pickle', 'AfterDeletesynapse-1.1.csv', 'synapse-1.2.pickle',
                    'AfterDeletesynapse-1.2.csv']
        , ['xalan-2.5.pickle', 'AfterDeletexalan-2.5.csv', 'xalan-2.6.pickle', 'AfterDeletexalan-2.6.csv'],
                   ['xerces-1.2.pickle', 'AfterDeletexerces-1.2.csv', 'xerces-1.3.pickle', 'AfterDeletexerces-1.3.csv']]
    tokenizerlist = ['camel-1.4tokenizer.pickle', 'jedit-4.0tokenizer.pickle', 'lucene-2.0tokenizer.pickle',
                     'poi-2.5tokenizer.pickle', 'synapse-1.1tokenizer.pickle',
                     'xalan-2.5tokenizer.pickle', 'xerces-1.2tokenizer.pickle']
    modelNamelist = ['attRnnCKcamel', 'attRnnCKjedit', 'attRnnCKlucene', 'attRnnCKpoi'
        , 'attRnnCKsynapse', 'attRnnCKxalan', 'attRnnCKxerces']
    attention_widths = [250, 500, 750, 1000, 1250, 1500, 1750, 2000]
    import datetime

    for i in range(3, 7):
        oldtime = datetime.datetime.now()
        training_list, training_listLabel, \
        testing_list, testing_listLabel, \
        train_traX, test_traX = dataload(projectList[i][0], projectList[i][1], projectList[i][2], projectList[i][3])
        train_set_x, train_set_y, \
        test_set_x, test_set_y, \
        validation_set_x, validation_set_y, \
        train_TraditionX, validation_TraditionX, Max_Len = getTrainTestValidSet(training_list, training_listLabel,
                                                                                testing_list, testing_listLabel,
                                                                                tokenizerlist[i], train_traX)

        token_dir = '../dataset/'
        tokenizer = LoadSaveData(token_dir + tokenizerlist[i])
        tokenizer.oov_token = None
        word_index = tokenizer.word_index
        for j in range(1):
            Self_Attention = Self_AttentionCK_Model(len(word_index) + 1, 30, Max_Len, 16,
                                                    attention_widths[j], 32, 20)
            Self_Attention.build_Model()
            Self_Attention.train_Model(train_set_x, train_set_y, 20, 8, validation_set_x, validation_set_y, '../model/',
                                       modelNamelist[i] + 'para' + str(j + 1), '../logs/', np.tanh(train_TraditionX),np.tanh(validation_TraditionX))
        newtime = datetime.datetime.now()
        print('{0}的训练时间为: {1:d} 小时'.format(projectList[i][0], (newtime - oldtime).seconds // 3600))
