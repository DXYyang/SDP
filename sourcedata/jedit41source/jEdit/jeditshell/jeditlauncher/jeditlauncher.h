
#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 5.03.0280 */
/* at Tue Jan 22 05:18:27 2002
 */
/* Compiler settings for I:\cvsfiles\jeditshell\jeditlauncher\jeditlauncher.idl:
    Oicf (OptLev=i2), W1, Zp8, env=Win32 (32b run), ms_ext, c_ext
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
//@@MIDL_FILE_HEADING(  )


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 440
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __jeditlauncher_h__
#define __jeditlauncher_h__

/* Forward Declarations */ 

#ifndef __IJEditLauncher_FWD_DEFINED__
#define __IJEditLauncher_FWD_DEFINED__
typedef interface IJEditLauncher IJEditLauncher;
#endif 	/* __IJEditLauncher_FWD_DEFINED__ */


#ifndef __JEditLauncher40_FWD_DEFINED__
#define __JEditLauncher40_FWD_DEFINED__

#ifdef __cplusplus
typedef class JEditLauncher40 JEditLauncher40;
#else
typedef struct JEditLauncher40 JEditLauncher40;
#endif /* __cplusplus */

#endif 	/* __JEditLauncher40_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

void __RPC_FAR * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void __RPC_FAR * ); 

#ifndef __IJEditLauncher_INTERFACE_DEFINED__
#define __IJEditLauncher_INTERFACE_DEFINED__

/* interface IJEditLauncher */
/* [unique][helpstring][dual][uuid][object] */ 

typedef /* [unique] */ IJEditLauncher __RPC_FAR *LPJEDITLAUNCHER;


EXTERN_C const IID IID_IJEditLauncher;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("E53269FA-8A5C-42B0-B3BC-82254F4FCED4")
    IJEditLauncher : public IDispatch
    {
    public:
        virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ServerKey( 
            /* [retval][out] */ ULONG __RPC_FAR *pKey) = 0;
        
        virtual /* [propget][id] */ HRESULT STDMETHODCALLTYPE get_ServerPort( 
            /* [retval][out] */ ULONG __RPC_FAR *pPort) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE OpenFile( 
            /* [in] */ BSTR bstrFileName) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE OpenFiles( 
            /* [in] */ VARIANTARG fileNames) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE Launch( void) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE RunScript( 
            /* [in] */ BSTR bstrFileName) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE EvalScript( 
            /* [in] */ BSTR bstrScript) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE RunDiff( 
            /* [in] */ BSTR bstrFileBase,
            /* [in] */ BSTR bstrFileChanged) = 0;
        
        virtual HRESULT STDMETHODCALLTYPE RunDiff_Var( 
            /* [in] */ VARIANTARG fileNames) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IJEditLauncherVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *QueryInterface )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void __RPC_FAR *__RPC_FAR *ppvObject);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *AddRef )( 
            IJEditLauncher __RPC_FAR * This);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *Release )( 
            IJEditLauncher __RPC_FAR * This);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetTypeInfoCount )( 
            IJEditLauncher __RPC_FAR * This,
            /* [out] */ UINT __RPC_FAR *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetTypeInfo )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo __RPC_FAR *__RPC_FAR *ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *GetIDsOfNames )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR __RPC_FAR *rgszNames,
            /* [in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID __RPC_FAR *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *Invoke )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS __RPC_FAR *pDispParams,
            /* [out] */ VARIANT __RPC_FAR *pVarResult,
            /* [out] */ EXCEPINFO __RPC_FAR *pExcepInfo,
            /* [out] */ UINT __RPC_FAR *puArgErr);
        
        /* [propget][id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *get_ServerKey )( 
            IJEditLauncher __RPC_FAR * This,
            /* [retval][out] */ ULONG __RPC_FAR *pKey);
        
        /* [propget][id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *get_ServerPort )( 
            IJEditLauncher __RPC_FAR * This,
            /* [retval][out] */ ULONG __RPC_FAR *pPort);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *OpenFile )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ BSTR bstrFileName);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *OpenFiles )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ VARIANTARG fileNames);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *Launch )( 
            IJEditLauncher __RPC_FAR * This);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *RunScript )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ BSTR bstrFileName);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *EvalScript )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ BSTR bstrScript);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE __RPC_FAR *RunDiff )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ BSTR bstrFileBase,
            /* [in] */ BSTR bstrFileChanged);
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *RunDiff_Var )( 
            IJEditLauncher __RPC_FAR * This,
            /* [in] */ VARIANTARG fileNames);
        
        END_INTERFACE
    } IJEditLauncherVtbl;

    interface IJEditLauncher
    {
        CONST_VTBL struct IJEditLauncherVtbl __RPC_FAR *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IJEditLauncher_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IJEditLauncher_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IJEditLauncher_Release(This)	\
    (This)->lpVtbl -> Release(This)


#define IJEditLauncher_GetTypeInfoCount(This,pctinfo)	\
    (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo)

#define IJEditLauncher_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo)

#define IJEditLauncher_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)

#define IJEditLauncher_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)


#define IJEditLauncher_get_ServerKey(This,pKey)	\
    (This)->lpVtbl -> get_ServerKey(This,pKey)

#define IJEditLauncher_get_ServerPort(This,pPort)	\
    (This)->lpVtbl -> get_ServerPort(This,pPort)

#define IJEditLauncher_OpenFile(This,bstrFileName)	\
    (This)->lpVtbl -> OpenFile(This,bstrFileName)

#define IJEditLauncher_OpenFiles(This,fileNames)	\
    (This)->lpVtbl -> OpenFiles(This,fileNames)

#define IJEditLauncher_Launch(This)	\
    (This)->lpVtbl -> Launch(This)

#define IJEditLauncher_RunScript(This,bstrFileName)	\
    (This)->lpVtbl -> RunScript(This,bstrFileName)

#define IJEditLauncher_EvalScript(This,bstrScript)	\
    (This)->lpVtbl -> EvalScript(This,bstrScript)

#define IJEditLauncher_RunDiff(This,bstrFileBase,bstrFileChanged)	\
    (This)->lpVtbl -> RunDiff(This,bstrFileBase,bstrFileChanged)

#define IJEditLauncher_RunDiff_Var(This,fileNames)	\
    (This)->lpVtbl -> RunDiff_Var(This,fileNames)

#endif /* COBJMACROS */


#endif 	/* C style interface */



/* [propget][id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_get_ServerKey_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [retval][out] */ ULONG __RPC_FAR *pKey);


void __RPC_STUB IJEditLauncher_get_ServerKey_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [propget][id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_get_ServerPort_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [retval][out] */ ULONG __RPC_FAR *pPort);


void __RPC_STUB IJEditLauncher_get_ServerPort_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_OpenFile_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [in] */ BSTR bstrFileName);


void __RPC_STUB IJEditLauncher_OpenFile_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_OpenFiles_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [in] */ VARIANTARG fileNames);


void __RPC_STUB IJEditLauncher_OpenFiles_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_Launch_Proxy( 
    IJEditLauncher __RPC_FAR * This);


void __RPC_STUB IJEditLauncher_Launch_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_RunScript_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [in] */ BSTR bstrFileName);


void __RPC_STUB IJEditLauncher_RunScript_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_EvalScript_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [in] */ BSTR bstrScript);


void __RPC_STUB IJEditLauncher_EvalScript_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


/* [id] */ HRESULT STDMETHODCALLTYPE IJEditLauncher_RunDiff_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [in] */ BSTR bstrFileBase,
    /* [in] */ BSTR bstrFileChanged);


void __RPC_STUB IJEditLauncher_RunDiff_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);


HRESULT STDMETHODCALLTYPE IJEditLauncher_RunDiff_Var_Proxy( 
    IJEditLauncher __RPC_FAR * This,
    /* [in] */ VARIANTARG fileNames);


void __RPC_STUB IJEditLauncher_RunDiff_Var_Stub(
    IRpcStubBuffer *This,
    IRpcChannelBuffer *_pRpcChannelBuffer,
    PRPC_MESSAGE _pRpcMessage,
    DWORD *_pdwStubPhase);



#endif 	/* __IJEditLauncher_INTERFACE_DEFINED__ */



#ifndef __JEDITLAUNCHERLib_LIBRARY_DEFINED__
#define __JEDITLAUNCHERLib_LIBRARY_DEFINED__

/* library JEDITLAUNCHERLib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_JEDITLAUNCHERLib;

EXTERN_C const CLSID CLSID_JEditLauncher40;

#ifdef __cplusplus

class DECLSPEC_UUID("6F58631E-D9C6-465C-B3A2-603F3F3D918C")
JEditLauncher40;
#endif
#endif /* __JEDITLAUNCHERLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long __RPC_FAR *, unsigned long            , BSTR __RPC_FAR * ); 
unsigned char __RPC_FAR * __RPC_USER  BSTR_UserMarshal(  unsigned long __RPC_FAR *, unsigned char __RPC_FAR *, BSTR __RPC_FAR * ); 
unsigned char __RPC_FAR * __RPC_USER  BSTR_UserUnmarshal(unsigned long __RPC_FAR *, unsigned char __RPC_FAR *, BSTR __RPC_FAR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long __RPC_FAR *, BSTR __RPC_FAR * ); 

unsigned long             __RPC_USER  VARIANT_UserSize(     unsigned long __RPC_FAR *, unsigned long            , VARIANT __RPC_FAR * ); 
unsigned char __RPC_FAR * __RPC_USER  VARIANT_UserMarshal(  unsigned long __RPC_FAR *, unsigned char __RPC_FAR *, VARIANT __RPC_FAR * ); 
unsigned char __RPC_FAR * __RPC_USER  VARIANT_UserUnmarshal(unsigned long __RPC_FAR *, unsigned char __RPC_FAR *, VARIANT __RPC_FAR * ); 
void                      __RPC_USER  VARIANT_UserFree(     unsigned long __RPC_FAR *, VARIANT __RPC_FAR * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


