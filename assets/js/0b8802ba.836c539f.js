"use strict";(self.webpackChunktaier_website=self.webpackChunktaier_website||[]).push([[340],{3905:function(e,t,r){r.d(t,{Zo:function(){return s},kt:function(){return d}});var n=r(7294);function a(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function c(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){a(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function u(e,t){if(null==e)return{};var r,n,a=function(e,t){if(null==e)return{};var r,n,a={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(a[r]=e[r]);return a}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var o=n.createContext({}),l=function(e){var t=n.useContext(o),r=t;return e&&(r="function"==typeof e?e(t):c(c({},t),e)),r},s=function(e){var t=l(e.components);return n.createElement(o.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},f=n.forwardRef((function(e,t){var r=e.components,a=e.mdxType,i=e.originalType,o=e.parentName,s=u(e,["components","mdxType","originalType","parentName"]),f=l(r),d=a,m=f["".concat(o,".").concat(d)]||f[d]||p[d]||i;return r?n.createElement(m,c(c({ref:t},s),{},{components:r})):n.createElement(m,c({ref:t},s))}));function d(e,t){var r=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var i=r.length,c=new Array(i);c[0]=f;var u={};for(var o in t)hasOwnProperty.call(t,o)&&(u[o]=t[o]);u.originalType=e,u.mdxType="string"==typeof e?e:a,c[1]=u;for(var l=2;l<i;l++)c[l]=r[l];return n.createElement.apply(null,c)}return n.createElement.apply(null,r)}f.displayName="MDXCreateElement"},1825:function(e,t,r){r.r(t),r.d(t,{frontMatter:function(){return u},contentTitle:function(){return o},metadata:function(){return l},toc:function(){return s},default:function(){return f}});var n=r(7462),a=r(3366),i=(r(7294),r(3905)),c=["components"],u={title:"\u67b6\u6784\u8bbe\u8ba1",sidebar_label:"\u67b6\u6784\u8bbe\u8ba1"},o="\u67b6\u6784\u8bbe\u8ba1",l={unversionedId:"guides/taier-architecture",id:"guides/taier-architecture",title:"\u67b6\u6784\u8bbe\u8ba1",description:"taier-architecture",source:"@site/docs/guides/taier-architecture.md",sourceDirName:"guides",slug:"/guides/taier-architecture",permalink:"/Taier/docs/guides/taier-architecture",editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/guides/taier-architecture.md",tags:[],version:"current",frontMatter:{title:"\u67b6\u6784\u8bbe\u8ba1",sidebar_label:"\u67b6\u6784\u8bbe\u8ba1"},sidebar:"docs",previous:{title:"\u540d\u79f0\u89e3\u91ca",permalink:"/Taier/docs/guides/explain"},next:{title:"\u4f9d\u8d56\u7ec4\u4ef6",permalink:"/Taier/docs/quickstart/rely"}},s=[{value:"taier \u4e0e DatasourceX\u3001Chunjun \u7684\u5173\u7cfb",id:"taier-\u4e0e-datasourcexchunjun-\u7684\u5173\u7cfb",children:[],level:2}],p={toc:s};function f(e){var t=e.components,u=(0,a.Z)(e,c);return(0,i.kt)("wrapper",(0,n.Z)({},p,u,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h1",{id:"\u67b6\u6784\u8bbe\u8ba1"},"\u67b6\u6784\u8bbe\u8ba1"),(0,i.kt)("p",null,(0,i.kt)("img",{alt:"taier-architecture",src:r(8326).Z})),(0,i.kt)("h2",{id:"taier-\u4e0e-datasourcexchunjun-\u7684\u5173\u7cfb"},"taier \u4e0e DatasourceX\u3001Chunjun \u7684\u5173\u7cfb"),(0,i.kt)("ul",null,(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("a",{parentName:"li",href:"https://github.com/DTStack/DatasourceX"},"DatasourceX")," \u662f\u6570\u636e\u6e90\u63d2\u4ef6\uff0c\u8d1f\u8d23\u5404\u7c7b\u578b\u6570\u636e\u6e90\u7684\u5143\u6570\u636e\u548c\u6570\u636e\u64cd\u4f5c\uff0c\u5982\u83b7\u53d6\u8868\u7ed3\u6784\uff0c\u9884\u89c8\u8868\u6570\u636e \u7b49\u529f\u80fd\u5747\u7531DatasourceX\u5b9e\u73b0\uff1b"),(0,i.kt)("li",{parentName:"ul"},(0,i.kt)("a",{parentName:"li",href:"https://github.com/DTStack/chunjun"},"Chunjun")," \u662f\u4e00\u4e2a\u57fa\u4e8eFlink\u7684\u6279\u6d41\u7edf\u4e00\u7684\u6570\u636e\u540c\u6b65\u5de5\u5177\uff0c\u65e2\u53ef\u4ee5\u91c7\u96c6\u9759\u6001\u7684\u6570\u636e\uff0c\u6bd4\u5982MySQL\uff0cHDFS\u7b49\uff0c\u4e5f\u53ef\u4ee5\u91c7\u96c6\u5b9e\u65f6\u53d8\u5316\u7684\u6570\u636e\uff0c\u6bd4\u5982MySQL binlog\uff0cKafka\u7b49\u3002")))}f.isMDXComponent=!0},8326:function(e,t,r){t.Z=r.p+"assets/images/taier-architecture-5dfa4a7251f3e120cf73a10942cce3cc.png"}}]);