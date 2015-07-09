-- using http://dublincore.org/schemas/xmls/qdc/dcterms.xsd because at http://dublincore.org/schemas/xmls/ it's the schema location for http://purl.org/dc/terms/ which is referenced in http://swordapp.github.io/SWORDv2-Profile/SWORDProfile.html
INSERT INTO foreignmetadataformatmapping(id, name, startelement, displayName, schemalocation) VALUES (1, 'http://purl.org/dc/terms/', 'entry', 'dcterms: DCMI Metadata Terms', 'http://dublincore.org/schemas/xmls/qdc/dcterms.xsd');
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (1, ':title', 'title', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (2, ':identifier', 'otherIdValue', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (3, ':creator', 'authorName', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (4, ':date', 'productionDate', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (5, ':subject', 'keywordValue', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (6, ':description', 'dsDescriptionValue', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (7, ':relation', 'relatedMaterial', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (8, ':isReferencedBy', 'publicationCitation', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (9, 'holdingsURI', 'publicationURL', TRUE, 8, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (10, 'agency', 'publicationIDType', TRUE, 8, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (11, 'IDNo', 'publicationIDNumber', TRUE, 8, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (12, ':coverage', 'otherGeographicCoverage', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (13, ':type', 'kindOfData', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (14, ':source', 'dataSources', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (15, 'affiliation', 'authorAffiliation', TRUE, 3, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (16, ':contributor', 'contributorName', FALSE, NULL, 1 );
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (17, 'type', 'contributorType', TRUE, 16, 1 );

/* ----------------------------------------
   Support the following per https://github.com/IQSS/dataverse/issues/2187

   - dcterms:spatial
   - dcterms:language (controlled vocabulary)
   - dcterms:identifier (with "agency" attribute)
*/ ----------------------------------------
INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (18, ':spatial', 'otherGeographicCoverage', FALSE, NULL, 1 );
/* ----------------------------------------
   FIXME: Apparently, controlled vocabulary is not supported by ImportGenericServiceBean.importXML
   because we get the exception below when we try to use <dcterms:language>English</dcterms:language>

   Line numbers as of commit 2479900

   https://github.com/IQSS/dataverse/blob/2479900f426f9e42c4ea0a5826334ddc39cdd824/src/main/java/edu/harvard/iq/dataverse/util/json/JsonParser.java#L310

[2015-07-09T09:22:36.426-0400] [glassfish 4.1] [SEVERE] [] [edu.harvard.iq.dataverse.api.imports.ImportGenericServiceBean] [tid: _ThreadID=101 _ThreadName=http-listener-2(3)] [timeMillis: 1436448156426] [levelValue: 1000] [[
  
edu.harvard.iq.dataverse.util.json.JsonParseException: incorrect  typeClass for field language, should be controlledVocabulary
	at edu.harvard.iq.dataverse.util.json.JsonParser.parseField(JsonParser.java:310)
	at edu.harvard.iq.dataverse.util.json.JsonParser.parseMetadataBlocks(JsonParser.java:232)
	at edu.harvard.iq.dataverse.util.json.JsonParser.parseDatasetVersion(JsonParser.java:212)
	at edu.harvard.iq.dataverse.api.imports.ImportGenericServiceBean.importXML(ImportGenericServiceBean.java:99)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:483)
	at org.glassfish.ejb.security.application.EJBSecurityManager.runMethod(EJBSecurityManager.java:1081)
	at org.glassfish.ejb.security.application.EJBSecurityManager.invoke(EJBSecurityManager.java:1153)
	at com.sun.ejb.containers.BaseContainer.invokeBeanMethod(BaseContainer.java:4786)
	at com.sun.ejb.EjbInvocation.invokeBeanMethod(EjbInvocation.java:656)
	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:822)
	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:608)
	at org.jboss.weld.ejb.AbstractEJBRequestScopeActivationInterceptor.aroundInvoke(AbstractEJBRequestScopeActivationInterceptor.java:46)
	at org.jboss.weld.ejb.SessionBeanInterceptor.aroundInvoke(SessionBeanInterceptor.java:52)
	at sun.reflect.GeneratedMethodAccessor5635.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:483)
	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:883)
	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:822)
	at com.sun.ejb.EjbInvocation.proceed(EjbInvocation.java:608)
	at com.sun.ejb.containers.interceptors.SystemInterceptorProxy.doCall(SystemInterceptorProxy.java:163)
	at com.sun.ejb.containers.interceptors.SystemInterceptorProxy.aroundInvoke(SystemInterceptorProxy.java:140)
	at sun.reflect.GeneratedMethodAccessor6210.invoke(Unknown Source)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:483)
	at com.sun.ejb.containers.interceptors.AroundInvokeInterceptor.intercept(InterceptorManager.java:883)
	at com.sun.ejb.containers.interceptors.AroundInvokeChainImpl.invokeNext(InterceptorManager.java:822)
	at com.sun.ejb.containers.interceptors.InterceptorManager.intercept(InterceptorManager.java:369)
	at com.sun.ejb.containers.BaseContainer.__intercept(BaseContainer.java:4758)
	at com.sun.ejb.containers.BaseContainer.intercept(BaseContainer.java:4746)
	at com.sun.ejb.containers.EJBLocalObjectInvocationHandler.invoke(EJBLocalObjectInvocationHandler.java:212)
	at com.sun.ejb.containers.EJBLocalObjectInvocationHandlerDelegate.invoke(EJBLocalObjectInvocationHandlerDelegate.java:88)
	at com.sun.proxy.$Proxy3363.importXML(Unknown Source)
	at edu.harvard.iq.dataverse.api.imports.__EJB31_Generated__ImportGenericServiceBean__Intf____Bean__.importXML(Unknown Source)
	at edu.harvard.iq.dataverse.api.datadeposit.CollectionDepositManagerImpl.createNew(CollectionDepositManagerImpl.java:109)
	at org.swordapp.server.CollectionAPI.post(CollectionAPI.java:165)
	at edu.harvard.iq.dataverse.api.datadeposit.SWORDv2CollectionServlet.doPost(SWORDv2CollectionServlet.java:35)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:707)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.apache.catalina.core.StandardWrapper.service(StandardWrapper.java:1682)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:344)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)
	at org.glassfish.tyrus.servlet.TyrusServletFilter.doFilter(TyrusServletFilter.java:295)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:256)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)
	at org.ocpsoft.rewrite.servlet.RewriteFilter.doFilter(RewriteFilter.java:205)
	at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:256)
	at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:214)
	at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:316)
	at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:160)
	at org.apache.catalina.core.StandardPipeline.doInvoke(StandardPipeline.java:734)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:673)
	at com.sun.enterprise.web.WebPipeline.invoke(WebPipeline.java:99)
	at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:174)
	at org.apache.catalina.core.StandardPipeline.doInvoke(StandardPipeline.java:734)
	at org.apache.catalina.core.StandardPipeline.invoke(StandardPipeline.java:673)
	at org.apache.catalina.connector.CoyoteAdapter.doService(CoyoteAdapter.java:412)
	at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:282)
	at com.sun.enterprise.v3.services.impl.ContainerMapper$HttpHandlerCallable.call(ContainerMapper.java:459)
	at com.sun.enterprise.v3.services.impl.ContainerMapper.service(ContainerMapper.java:167)
	at org.glassfish.grizzly.http.server.HttpHandler.runService(HttpHandler.java:201)
	at org.glassfish.grizzly.http.server.HttpHandler.doHandle(HttpHandler.java:175)
	at org.glassfish.grizzly.http.server.HttpServerFilter.handleRead(HttpServerFilter.java:235)
	at org.glassfish.grizzly.filterchain.ExecutorResolver$9.execute(ExecutorResolver.java:119)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.executeFilter(DefaultFilterChain.java:284)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.executeChainPart(DefaultFilterChain.java:201)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.execute(DefaultFilterChain.java:133)
	at org.glassfish.grizzly.filterchain.DefaultFilterChain.process(DefaultFilterChain.java:112)
	at org.glassfish.grizzly.ProcessorExecutor.execute(ProcessorExecutor.java:77)
	at org.glassfish.grizzly.nio.transport.TCPNIOTransport.fireIOEvent(TCPNIOTransport.java:561)
	at org.glassfish.grizzly.strategies.AbstractIOStrategy.fireIOEvent(AbstractIOStrategy.java:112)
	at org.glassfish.grizzly.strategies.WorkerThreadIOStrategy.run0(WorkerThreadIOStrategy.java:117)
	at org.glassfish.grizzly.strategies.WorkerThreadIOStrategy.access$100(WorkerThreadIOStrategy.java:56)
	at org.glassfish.grizzly.strategies.WorkerThreadIOStrategy$WorkerThreadRunnable.run(WorkerThreadIOStrategy.java:137)
	at org.glassfish.grizzly.threadpool.AbstractThreadPool$Worker.doWork(AbstractThreadPool.java:565)
	at org.glassfish.grizzly.threadpool.AbstractThreadPool$Worker.run(AbstractThreadPool.java:545)
	at java.lang.Thread.run(Thread.java:745)
]]

[2015-07-09T09:22:36.921-0400] [glassfish 4.1] [INFO] [] [edu.harvard.iq.dataverse.api.datadeposit.CollectionDepositManagerImpl] [tid: _ThreadID=101 _ThreadName=http-listener-2(3)] [timeMillis: 1436448156921] [levelValue: 800] [[
  Validation failed: Author Name is required. Title is required. Description Text is required.]]

*/ ----------------------------------------
-- INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (19, ':language', 'language', FALSE, NULL, 1 );
/* ----------------------------------------
   FIXME: Is it safe to drop the (foreignmetadataformatmapping_id, foreignfieldxpath)
   uniqueness constraint? See comment at ForeignMetadataFieldMapping.java
   For now we comment this second "agency" for "dcterms" out:
*/ ----------------------------------------
---INSERT INTO foreignmetadatafieldmapping (id, foreignfieldxpath, datasetfieldname, isattribute, parentfieldmapping_id, foreignmetadataformatmapping_id) VALUES (20, 'agency', 'otherIdAgency', TRUE, 2, 1 );

INSERT INTO guestbook(
             emailrequired, enabled, institutionrequired, createtime,
            "name", namerequired, positionrequired,  dataverse_id)
    VALUES (  false, true, false, now(),
            'Default', false, false, null);
