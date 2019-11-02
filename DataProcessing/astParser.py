import numpy as np
import pandas as pd
import javalang as jl
import javalang.tree as jlt
import pickle
import os
import re
from func.funcs import *


class AstParser:
    def __init__(self, sPath, oPath):
        """
        程序的AST树解析
        :param sPath: 下载项目路径
        :param oPath: AST解析后的项目路径
        """
        self.sPath = sPath
        self.oPath = oPath
        self.TypeList = ['FormalParameter', 'BasicType', 'PackageDeclaration',
                         'InterfaceDeclaration', 'CatchClauseParameter', 'ClassDeclaration',
                         'MethodInvocation', 'SuperMethodInvocation', 'MemberReference', 'SuperMemberReference',
                         'ConstructorDeclaration', 'ReferenceType', 'MethodDeclaration', 'VariableDeclarator',
                         'IfStatement',
                         'WhileStatement', 'DoStatement', 'ForStatement', 'AssertStatement', 'BreakStatement',
                         'ContinueStatement',
                         'ReturnStatement', 'ThrowStatement', 'SynchronizedStatement',
                         'TryStatement', 'SwitchStatement', 'BlockStatement',
                         'StatementExpression', 'TryResource', 'CatchClause', 'CatchClauseParameter',
                         'SwitchStatementCase', 'ForControl', 'EnhancedForControl']

    def saveAstMap(self, proSource, proCSV, reg=r'(org.+).java$'):
        saveName = proCSV[:-4] + 'ASTMap.pickle'
        projectPath = self.sPath + proSource + "\\"
        df = LoadCSV(self.sPath + proCSV)
        projectJavaMap = {}
        for root, dirs, files in os.walk(projectPath):
            for each in files:
                tempPath = os.path.join(root, each)
                searchResult = re.search(reg, tempPath)
                if searchResult:
                    resultList = []
                    temp = searchResult.group(1)
                    tempJpath = '.'.join(temp.split("\\"))
                    if tempJpath in df['name.1'].tolist():
                        with open(tempPath) as file:
                            JavaContents = file.read()
                        try:
                            tree = jl.parse.parse(JavaContents)
                        except Exception:
                            continue
                        else:
                            for path, node in tree:
                                if isinstance(node, jlt.MethodInvocation) or isinstance(node,
                                                                                        jlt.SuperMethodInvocation):
                                    resultList.append(str(node.member) + "()")
                                    continue
                                if isinstance(node, jlt.ClassCreator):
                                    resultList.append(str(node.type.name))
                                    continue
                                if str(node) in self.TypeList and isinstance(node, jlt.Declaration):
                                    resultList.append(str(node.name))
                                    continue
                                if isinstance(node, jlt.AssertStatement) or isinstance(node, jlt.TryResource):
                                    resultList.append(str(node.value))
                                    continue
                                if str(node) in self.TypeList:
                                    resultList.append(str(node))
                            projectJavaMap[tempJpath] = resultList
        savepickle(projectJavaMap, self.oPath + saveName)
        return saveName

    def Random_OverSample(self,imData):
        imDataPlace = self.sPath + imData
        tradition_list, _ = LoadTraditionCSV(imDataPlace)
        trainingDF_Bug = LoadCSV(imDataPlace)['bug'].tolist()
        training_listLabel = MakeLabels(trainingDF_Bug)
        from imblearn.over_sampling import RandomOverSampler
        ros = RandomOverSampler(random_state=0)
        X_res, y_res = ros.fit_sample(tradition_list, training_listLabel)
        result = [X_res, y_res]
        savepickle(result, self.oPath + "AfterOverSample" + imData[:-4] + ".pickle")
        return "AfterOverSample" + imData[:-4] + ".pickle"

    def test_sample(self,imData):
        imDataPlace = self.sPath + imData
        tradition_list, _ = LoadTraditionCSV(imDataPlace)
        trainingDF_Bug = LoadCSV(imDataPlace)['bug'].tolist()
        training_listLabel = MakeLabels(trainingDF_Bug)
        result = [tradition_list, training_listLabel.reshape((-1,))]
        savepickle(result, self.oPath + "AfterSample" + imData[:-4] + ".pickle")
        return "AfterSample" + imData[:-4] + ".pickle"

    def setTokenizer(self,AfterOverSampleData, AstMapData, imData):
        from keras.preprocessing.text import Tokenizer
        imDataPlace = self.sPath + imData
        AfterOverSampleDataPlace = self.oPath + AfterOverSampleData
        AstMapDataPlace = self.oPath + AstMapData
        _, dfdict = LoadTraditionCSV(imDataPlace)
        X, _ = loadpickle(AfterOverSampleDataPlace)
        dataAst = loadpickle(AstMapDataPlace)
        new_dfdict = {v: k for k, v in dfdict.items()}
        tokenList = []
        for item in X[:, 0]:
            if new_dfdict[item] in dataAst:
                tokenList.append(dataAst[new_dfdict[item]])

        tokenizer = Tokenizer(num_words=2000, split=' ', filters=':')
        tokenizer.fit_on_texts(JoinSubList(tokenList))
        savepickle(tokenizer, self.oPath + imData[:-4] + 'tokenizer.pickle')

    def SelectByAST(self,data, AstMapData, imData):
        dataPlace = self.oPath + data
        AstMapDataPlace = self.oPath+ AstMapData
        imDataPlace = self.sPath + imData
        _, dfdict = LoadTraditionCSV(imDataPlace)
        X, y = loadpickle(dataPlace)
        y = y.reshape((-1, 1))
        XandY = np.concatenate((X, y), axis=1)
        df = pd.DataFrame(XandY, columns=["name.1", "wmc", "dit", "noc", "cbo", "rfc", "lcom", "ca", "ce",
                                          "npm", "lcom3", "loc", "dam", "moa", "mfa",
                                          "cam", "ic", "cbm", "amc", "max_cc", "avg_cc", "bugs"])
        dataAst = loadpickle(AstMapDataPlace)
        new_dfdict = {v: k for k, v in dfdict.items()}
        tokenList = []
        deleteList = []
        for index, item in enumerate(df['name.1'].tolist()):
            if new_dfdict[item] not in dataAst:
                deleteList.append(index)
        df = df.drop(deleteList)
        df.to_csv(self.oPath + "AfterDelete" + imData[:-4] + ".csv", encoding="utf-8", index=False)
        for item in df['name.1']:
            tokenList.append(dataAst[new_dfdict[item]])
        savepickle(tokenList, self.oPath + imData[:-4] + ".pickle")


class AstFactory:
    def __init__(self,AstParser):
        self.AstParser=AstParser

    def astProcessing(self,TwoProjects,TwoCSV,reg):
        trainPro, testPro = TwoProjects
        trainCSV, testCSV = TwoCSV
        trainASPName = self.AstParser.saveAstMap(trainPro, trainCSV, reg)
        testASPName = self.AstParser.saveAstMap(testPro, testCSV, reg)
        trainSampleName = self.AstParser.Random_OverSample(trainCSV)
        testSampleName = self.AstParser.test_sample(testCSV)
        self.AstParser.setTokenizer(trainSampleName, trainASPName, trainCSV)
        self.AstParser.setTokenizer(testSampleName, testASPName, testCSV)
        self.AstParser.SelectByAST(trainSampleName, trainASPName, trainCSV)
        self.AstParser.SelectByAST(testSampleName, testASPName, testCSV)






if __name__ == '__main__':
    projects = [['camel-camel-1.4.0', 'camel-camel-1.6.0'],
                ['jedit40source', 'jedit41source'],
                ['lucene-solr-releases-lucene-2.0.0', 'lucene-solr-releases-lucene-2.2.0'],
                ['poi-REL_2_5_1', 'poi-REL_3_0'],
                ['synapse-1.1', 'synapse-1.2'],
                ['xalan-j-xalan-j_2_5_0', 'xalan-j-xalan-j_2_6_0'],
                ['xerces2-j-Xerces-J_1_2_0', 'xerces2-j-Xerces-J_1_3_0']
                ]
    projectCSVs = [['camel-1.4.csv', 'camel-1.6.csv'], ['jedit-4.0.csv', 'jedit-4.1.csv'],
                   ['lucene-2.0.csv', 'lucene-2.2.csv'], ['poi-2.5.csv', 'poi-3.0.csv'],
                   ['synapse-1.1.csv', 'synapse-1.2.csv'],
                   ['xalan-2.5.csv', 'xalan-2.6.csv'],
                   ['xerces-1.2.csv', 'xerces-1.3.csv']]
    reglist = [r'(org.+).java$', r'(org.+|bsh.+|gnu.+).java$',
               r'(org.+).java$', r'(org.+).java$',
               r'(org.+).java$', r'(org.+).java$',
               r'(org.+|javax.+).java$']

    sourcePath = "..\\sourcedata\\"
    outputPath = "..\\dataset\\"

    AstParser=AstParser(sourcePath,outputPath)
    AstFactory=AstFactory(AstParser)
    for i in range(len(projects)):
        AstFactory.astProcessing(projects[i],projectCSVs[i],reglist[i])
