import * as api from './api';

// This class correctly sets the base path

let realBasePath = api.BASE_PATH;

function getApplicationBasePath() {
    let applicationBasePathT = window.location.pathname.toLowerCase();

    if(applicationBasePathT.indexOf(".html") !== -1) {
        applicationBasePathT = applicationBasePathT.substring(0, applicationBasePathT.indexOf(".html")+5) + "/";
    }else {
        applicationBasePathT= "/";
    }
    return applicationBasePathT;
}

function getApplicationFilesBase() {
    return getApplicationBasePath().replace("/index.html/", "") + "/";
}

if(["localhost", "127.0.0.1", "::1"].indexOf(window.location.hostname) === -1) {
    realBasePath = getApplicationFilesBase() + "backend"; // Live
}
if(window.location.hash) {
    const valueMap = window.location.hash.substring(1).split('&').map(s=><[string, string]> s.split('='));
    let tokenItem = valueMap.filter(s => s[0] ==='token')[0];
    setToken(tokenItem[1]);
}
export function setToken(token: string) {
    localStorage.setItem("token", token);
    api.defaultHeaders["Authorization"] = "Bearer " + localStorage.getItem("token");
}

if(localStorage.getItem("token")) {
    api.defaultHeaders["Authorization"] = "Bearer " + localStorage.getItem("token");
}

export const PresentationcontrollerApi = class extends api.PresentationcontrollerApi {
    constructor() {
        super(realBasePath);
    }
}
export const UsercontrollerApi = class extends api.UsercontrollerApi {
    constructor() {
        super(realBasePath);
    }
}
export const SubscriptioncontrollerApi = class extends api.SubscriptioncontrollerApi {
    constructor() {
        super(realBasePath);
    }
}
export const PresentationmailcontrollerApi = class extends api.PresentationmailcontrollerApi {
    constructor() {
        super(realBasePath);
    }
}

export const UseradmincontrollerApi = class extends api.UseradmincontrollerApi {
    constructor() {
        super(realBasePath);
    }
}

export const PresentationfilecontrollerApi = class extends api.PresentationfilecontrollerApi {
    constructor() {
        super(realBasePath);
    }
}

export type UserAddMeta = api.UserAddMeta;
export type UserMeta = api.UserMeta;
export type Presentation = api.Presentation;
export type PresentationFileMeta = api.PresentationFileMeta;
export type Subscription = api.Subscription;
export type EmailView = api.EmailView;

export const applicationBasePath = getApplicationBasePath();
export const applicatonFilesBase = getApplicationFilesBase();
export const backendPath = realBasePath;