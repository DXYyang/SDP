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
 * Created on May 14, 2005
 *
 */
package org.apache.poi.hssf.record.formula.eval;

/**
 * @author Amol S. Deshmukh &lt; amolweb at ya hoo dot com &gt;
 *
 */
public class ValueEvalToNumericXlator {

    public static final int STRING_IS_PARSED = 0x0001;
    public static final int BOOL_IS_PARSED = 0x0002;
    public static final int BLANK_IS_PARSED = 0x0004; // => blanks are not ignored, converted to 0
    
    public static final int REF_STRING_IS_PARSED = 0x0008;
    public static final int REF_BOOL_IS_PARSED = 0x0010;
    public static final int REF_BLANK_IS_PARSED = 0x0020;
    
    public static final int EVALUATED_REF_STRING_IS_PARSED = 0x0040;
    public static final int EVALUATED_REF_BOOL_IS_PARSED = 0x0080;
    public static final int EVALUATED_REF_BLANK_IS_PARSED = 0x0100;
    
    public static final int STRING_TO_BOOL_IS_PARSED = 0x0200;
    public static final int REF_STRING_TO_BOOL_IS_PARSED = 0x0400;
    
    public static final int STRING_IS_INVALID_VALUE = 0x0800;
    public static final int REF_STRING_IS_INVALID_VALUE = 0x1000;
    
//    public static final int BOOL_IS_BLANK = 0x2000;
//    public static final int REF_BOOL_IS_BLANK = 0x4000;
//    public static final int STRING_IS_BLANK = 0x8000;
//    public static final int REF_STRING_IS_BLANK = 0x10000;
    
    private final int flags;
    
    
    public ValueEvalToNumericXlator(int flags) {
        this.flags = flags;
    }
    
    /**
     * returned value can be either A NumericValueEval, BlankEval or ErrorEval.
     * The params can be either NumberEval, BoolEval, StringEval, or
     * RefEval
     * @param eval
     * @return
     */
    public ValueEval attemptXlateToNumeric(ValueEval eval) {
        ValueEval retval = null;
        
        if (eval == null) {
            retval = BlankEval.INSTANCE;
        }
        
        // most common case - least worries :)
        else if (eval instanceof NumberEval) {
            retval = (NumberEval) eval; 
        }
        
        // booleval
        else if (eval instanceof BoolEval) {
            retval = ((flags & BOOL_IS_PARSED) > 0)
                ? (NumericValueEval) eval
                : xlateBlankEval(BLANK_IS_PARSED);
        } 
        
        // stringeval 
        else if (eval instanceof StringEval) {
            retval = xlateStringEval((StringEval) eval); // TODO: recursive call needed
        }
        
        // refeval
        else if (eval instanceof RefEval) {
            retval = xlateRefEval((RefEval) eval);
        }
        
        // erroreval
        else if (eval instanceof ErrorEval) {
            retval = eval;
        }
        
        else if (eval instanceof BlankEval) {
            retval = xlateBlankEval(BLANK_IS_PARSED);
        }
        
        // probably AreaEval? then not acceptable.
        else {
            throw new RuntimeException("Invalid ValueEval type passed for conversion: " + eval.getClass());
        }
        
        return retval;
    }
    
    /**
     * no args are required since BlankEval has only one 
     * instance. If flag is set, a zero
     * valued numbereval is returned, else BlankEval.INSTANCE
     * is returned.
     * @return
     */
    private ValueEval xlateBlankEval(int flag) {
        return ((flags & flag) > 0)
                ? (ValueEval) NumberEval.ZERO
                : BlankEval.INSTANCE;
    }
    
    /**
     * uses the relevant flags to decode the supplied RefVal
     * @param eval
     * @return
     */
    private ValueEval xlateRefEval(RefEval reval) {
        ValueEval retval = null;
        ValueEval eval = (ValueEval) reval.getInnerValueEval();
        
        // most common case - least worries :)
        if (eval instanceof NumberEval) {
            retval = (NumberEval) eval;
        }
        
        // booleval
        else if (eval instanceof BoolEval) {
            retval = ((flags & REF_BOOL_IS_PARSED) > 0)
                    ? (ValueEval) eval
                    : BlankEval.INSTANCE;
        } 
        
        // stringeval 
        else if (eval instanceof StringEval) {
            retval = xlateRefStringEval((StringEval) eval);
        }
        
        // erroreval
        else if (eval instanceof ErrorEval) {
            retval = eval;
        }
        
        // refeval
        else if (eval instanceof RefEval) {
            RefEval re = (RefEval) eval;
            retval = xlateRefEval(re);
        }
        
        else if (eval instanceof BlankEval) {
            retval = xlateBlankEval(reval.isEvaluated() ? EVALUATED_REF_BLANK_IS_PARSED : REF_BLANK_IS_PARSED);
        }
        
        // probably AreaEval ? then not acceptable.
        else { 
            throw new RuntimeException("Invalid ValueEval type passed for conversion: " + eval.getClass());
        }

        
        

        return retval;
    }
    
    /**
     * uses the relevant flags to decode the StringEval
     * @param eval
     * @return
     */
    private ValueEval xlateStringEval(StringEval eval) {
        ValueEval retval = null;
        if ((flags & STRING_IS_PARSED) > 0) {
            String s = eval.getStringValue();
            try { 
                double d = Double.parseDouble(s);
                retval = new NumberEval(d);
            } 
            catch (Exception e) {
                if ((flags & STRING_TO_BOOL_IS_PARSED) > 0) {
                    try { 
                        boolean b = Boolean.getBoolean(s);
                        retval = b ? BoolEval.TRUE : BoolEval.FALSE;
                    } 
                    catch (Exception e2) { retval = ErrorEval.VALUE_INVALID; }
                }
                else {
                    retval = ErrorEval.VALUE_INVALID;
                }
            }
        }
        else if ((flags & STRING_TO_BOOL_IS_PARSED) > 0) {
            String s = eval.getStringValue();
            try { 
                boolean b = Boolean.getBoolean(s);
                retval = b ? BoolEval.TRUE : BoolEval.FALSE;
            } 
            catch (Exception e) { retval = ErrorEval.VALUE_INVALID; }
        }
        
        // strings are errors?
        else if ((flags & STRING_IS_INVALID_VALUE) > 0) {
            retval = ErrorEval.VALUE_INVALID;
        }
        
        // ignore strings
        else {
            retval = xlateBlankEval(BLANK_IS_PARSED);
        }
        return retval;
    }
    
    /**
     * uses the relevant flags to decode the StringEval
     * @param eval
     * @return
     */
    private ValueEval xlateRefStringEval(StringEval eval) {
        ValueEval retval = null;
        if ((flags & REF_STRING_IS_PARSED) > 0) {
            StringEval sve = (StringEval) eval;
            String s = sve.getStringValue();
            try { 
                double d = Double.parseDouble(s);
                retval = new NumberEval(d);
            } 
            catch (Exception e) { 
                if ((flags & REF_STRING_TO_BOOL_IS_PARSED) > 0) {
                    try { 
                        boolean b = Boolean.getBoolean(s);
                        retval = b ? BoolEval.TRUE : BoolEval.FALSE;
                    } 
                    catch (Exception e2) { retval = ErrorEval.VALUE_INVALID; }
                }
                else {
                    retval = ErrorEval.VALUE_INVALID;
                }
            }
        }
        else if ((flags & REF_STRING_TO_BOOL_IS_PARSED) > 0) {
            StringEval sve = (StringEval) eval;
            String s = sve.getStringValue();
            try { 
                boolean b = Boolean.getBoolean(s);
                retval = b ? BoolEval.TRUE : BoolEval.FALSE;;
            } 
            catch (Exception e) { retval = ErrorEval.VALUE_INVALID; }
        }
        
        // strings are errors?
        else if ((flags & REF_STRING_IS_INVALID_VALUE) > 0) {
            retval = ErrorEval.VALUE_INVALID;
        }
        
        // strings are blanks
        else {
            retval = BlankEval.INSTANCE;
        }
        return retval;
    }
    
}
