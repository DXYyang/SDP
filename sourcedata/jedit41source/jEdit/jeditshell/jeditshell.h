
#pragma warning( disable: 4049 )  /* more than 64k source lines */

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 5.03.0280 */
/* at Tue Jul 31 16:38:14 2001
 */
/* Compiler settings for I:\cvsfiles\jeditshell\jeditshell.idl:
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

#ifndef __jeditshell_h__
#define __jeditshell_h__

/* Forward Declarations */ 

#ifndef __IJEditCtxMenu_FWD_DEFINED__
#define __IJEditCtxMenu_FWD_DEFINED__
typedef interface IJEditCtxMenu IJEditCtxMenu;
#endif 	/* __IJEditCtxMenu_FWD_DEFINED__ */


#ifndef __JEditCtxMenu_FWD_DEFINED__
#define __JEditCtxMenu_FWD_DEFINED__

#ifdef __cplusplus
typedef class JEditCtxMenu JEditCtxMenu;
#else
typedef struct JEditCtxMenu JEditCtxMenu;
#endif /* __cplusplus */

#endif 	/* __JEditCtxMenu_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 

void __RPC_FAR * __RPC_USER MIDL_user_allocate(size_t);
void __RPC_USER MIDL_user_free( void __RPC_FAR * ); 

/* interface __MIDL_itf_jeditshell_0000 */
/* [local] */ 

#include <shlobj.h>


extern RPC_IF_HANDLE __MIDL_itf_jeditshell_0000_v0_0_c_ifspec;
extern RPC_IF_HANDLE __MIDL_itf_jeditshell_0000_v0_0_s_ifspec;

#ifndef __IJEditCtxMenu_INTERFACE_DEFINED__
#define __IJEditCtxMenu_INTERFACE_DEFINED__

/* interface IJEditCtxMenu */
/* [unique][helpstring][uuid][object] */ 

typedef /* [unique] */ IJEditCtxMenu __RPC_FAR *LPJEDITCTXMENU;


EXTERN_C const IID IID_IJEditCtxMenu;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("378023D8-0B36-4743-958B-43209CCD311D")
    IJEditCtxMenu : public IUnknown
    {
    public:
    };
    
#else 	/* C style interface */

    typedef struct IJEditCtxMenuVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE __RPC_FAR *QueryInterface )( 
            IJEditCtxMenu __RPC_FAR * This,
            /* [in] */ REFIID riid,
            /* [iid_is][out] */ void __RPC_FAR *__RPC_FAR *ppvObject);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *AddRef )( 
            IJEditCtxMenu __RPC_FAR * This);
        
        ULONG ( STDMETHODCALLTYPE __RPC_FAR *Release )( 
            IJEditCtxMenu __RPC_FAR * This);
        
        END_INTERFACE
    } IJEditCtxMenuVtbl;

    interface IJEditCtxMenu
    {
        CONST_VTBL struct IJEditCtxMenuVtbl __RPC_FAR *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IJEditCtxMenu_QueryInterface(This,riid,ppvObject)	\
    (This)->lpVtbl -> QueryInterface(This,riid,ppvObject)

#define IJEditCtxMenu_AddRef(This)	\
    (This)->lpVtbl -> AddRef(This)

#define IJEditCtxMenu_Release(This)	\
    (This)->lpVtbl -> Release(This)


#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IJEditCtxMenu_INTERFACE_DEFINED__ */



#ifndef __JEDITCTXMENULib_LIBRARY_DEFINED__
#define __JEDITCTXMENULib_LIBRARY_DEFINED__

/* library JEDITCTXMENULib */
/* [helpstring][version][uuid] */ 


EXTERN_C const IID LIBID_JEDITCTXMENULib;

EXTERN_C const CLSID CLSID_JEditCtxMenu;

#ifdef __cplusplus

class DECLSPEC_UUID("2FA37216-6AFA-4299-9203-8A962384AA7E")
JEditCtxMenu;
#endif
#endif /* __JEDITCTXMENULib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


