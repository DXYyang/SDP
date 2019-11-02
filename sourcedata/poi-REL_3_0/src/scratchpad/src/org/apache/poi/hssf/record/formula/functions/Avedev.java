/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on May 15, 2005
 *
 */
package org.apache.poi.hssf.record.formula.functions;

import org.apache.poi.hssf.record.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.formula.eval.Eval;
import org.apache.poi.hssf.record.formula.eval.NumberEval;
import org.apache.poi.hssf.record.formula.eval.ValueEval;
import org.apache.poi.hssf.record.formula.eval.ValueEvalToNumericXlator;

/**
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 *
 */
public class Avedev extends MultiOperandNumericFunction {

    private static final ValueEvalToNumericXlator DEFAULT_NUM_XLATOR =
        new ValueEvalToNumericXlator((short) (0
              //    ValueEvalToNumericXlator.BOOL_IS_PARSED  
              //| ValueEvalToNumericXlator.REF_BOOL_IS_PARSED  
              //| ValueEvalToNumericXlator.EVALUATED_REF_BOOL_IS_PARSED  
                | ValueEvalToNumericXlator.STRING_IS_PARSED  
              //| ValueEvalToNumericXlator.REF_STRING_IS_PARSED  
              //| ValueEvalToNumericXlator.EVALUATED_REF_STRING_IS_PARSED  
              //| ValueEvalToNumericXlator.STRING_TO_BOOL_IS_PARSED  
              //| ValueEvalToNumericXlator.REF_STRING_TO_BOOL_IS_PARSED  
              //| ValueEvalToNumericXlator.STRING_IS_INVALID_VALUE  
              //| ValueEvalToNumericXlator.REF_STRING_IS_INVALID_VALUE
                | ValueEvalToNumericXlator.EVALUATED_REF_BLANK_IS_PARSED
                ));
    
    /**
     * this is the default impl for the factory method getXlator
     * of the super class NumericFunction. Subclasses can override this method
     * if they desire to return a different ValueEvalToNumericXlator instance
     * than the default.
     */
    protected ValueEvalToNumericXlator getXlator() {
        return DEFAULT_NUM_XLATOR;
    }

    
    
    public Eval evaluate(Eval[] operands, int srcCellRow, short srcCellCol) {
        ValueEval retval = null;
        double[] values = getNumberArray(operands, srcCellRow, srcCellCol);
        if (values == null) {
            retval = ErrorEval.VALUE_INVALID;
        }
        else {
            double d = StatsLib.avedev(values);
            retval = (Double.isNaN(d) || Double.isInfinite(d))
                    ? (ValueEval) ErrorEval.NUM_ERROR
                    : new NumberEval(d);
        }
        
        return retval;
    }
}
