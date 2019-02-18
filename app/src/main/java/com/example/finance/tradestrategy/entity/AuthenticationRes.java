package com.example.finance.tradestrategy.entity;

import java.util.List;

/**
 * Created by Administrator on 2017/8/22.
 *
 */

public class AuthenticationRes extends BaseResponse {


    /**
     * data : {"status":{"status":"unaccount","web_signup":null,"permission":["usStockQuoteLv0","hkStockQuoteLv0"],"status_code":0,"set_trade_password":false,"step":["开户","审核","入金","成功"],"link":"https://www.itiger.com/accounts_continue","hk_left_days":0,"status_description":"","portal_text":"马上开户","open_version":0,"sdk_info":{"btn_text":"马上开户","code":0,"link":"https://www.itiger.com/accounts_continue","step":["开户","审核","入金","成功"]},"a_stock_sdk":{"open_account_sdk":false,"trade_sdk":false},"sns_status":{"wechat_binding":false,"xiaomi_binding":false,"wxapp_binding":false,"weibo_binding":false,"facebook_binding":false,"twitter_binding":false},"btn_text":"马上开户","role":"guest","open_version_ios":0,"status_message":"未注册"},"scopes":"common read write guest","uuid":11487959974,"access_token":"VqBQVo27yVMt73JjDGC93TcJs4yU5yE5F1TLtb4Gd8b8AXIMsc","tel_code":null,"expires_in":2592000,"new_status":{"status":"unaccount","web_signup":null,"permission":["usStockQuoteLv0","hkStockQuoteLv0"],"status_code":0,"sdk_info":{"btn_text":"马上开户","code":0,"link":"https://www.itiger.com/accounts_continue","step":["开户","审核","入金","成功"]},"step":["开户","审核","入金","成功"],"sns_status":{"wechat_binding":false,"xiaomi_binding":false,"wxapp_binding":false,"weibo_binding":false,"facebook_binding":false,"twitter_binding":false},"a_stock_sdk":{"open_account_sdk":false,"trade_sdk":false},"open_version":0,"btn_text":"马上开户","link":"https://www.itiger.com/accounts_continue","hk_left_days":0,"open_version_ios":0,"role":"guest","set_trade_password":false,"portal_text":"马上开户"},"invite_code":null,"token_type":"Guest","phone":null,"wxappid":null,"emai":null,"id":0,"refresh_token":"h22B32wW7E4yZpALqwTe98108lbxG0OFtyDyVt92ypi6s8XQl6"}
     * is_succ : true
     */

    private DataBean data;
    private boolean is_succ;

    public static class DataBean {
        /**
         * status : {"status":"unaccount","web_signup":null,"permission":["usStockQuoteLv0","hkStockQuoteLv0"],"status_code":0,"set_trade_password":false,"step":["开户","审核","入金","成功"],"link":"https://www.itiger.com/accounts_continue","hk_left_days":0,"status_description":"","portal_text":"马上开户","open_version":0,"sdk_info":{"btn_text":"马上开户","code":0,"link":"https://www.itiger.com/accounts_continue","step":["开户","审核","入金","成功"]},"a_stock_sdk":{"open_account_sdk":false,"trade_sdk":false},"sns_status":{"wechat_binding":false,"xiaomi_binding":false,"wxapp_binding":false,"weibo_binding":false,"facebook_binding":false,"twitter_binding":false},"btn_text":"马上开户","role":"guest","open_version_ios":0,"status_message":"未注册"}
         * scopes : common read write guest
         * uuid : 11487959974
         * access_token : VqBQVo27yVMt73JjDGC93TcJs4yU5yE5F1TLtb4Gd8b8AXIMsc
         * tel_code : null
         * expires_in : 2592000
         * new_status : {"status":"unaccount","web_signup":null,"permission":["usStockQuoteLv0","hkStockQuoteLv0"],"status_code":0,"sdk_info":{"btn_text":"马上开户","code":0,"link":"https://www.itiger.com/accounts_continue","step":["开户","审核","入金","成功"]},"step":["开户","审核","入金","成功"],"sns_status":{"wechat_binding":false,"xiaomi_binding":false,"wxapp_binding":false,"weibo_binding":false,"facebook_binding":false,"twitter_binding":false},"a_stock_sdk":{"open_account_sdk":false,"trade_sdk":false},"open_version":0,"btn_text":"马上开户","link":"https://www.itiger.com/accounts_continue","hk_left_days":0,"open_version_ios":0,"role":"guest","set_trade_password":false,"portal_text":"马上开户"}
         * invite_code : null
         * token_type : Guest
         * phone : null
         * wxappid : null
         * emai : null
         * id : 0
         * refresh_token : h22B32wW7E4yZpALqwTe98108lbxG0OFtyDyVt92ypi6s8XQl6
         */

        private StatusBean status;
        private String scopes;
        private long uuid;
        private String access_token;
        private Object tel_code;
        private int expires_in;
        private NewStatusBean new_status;
        private Object invite_code;
        private String token_type;
        private Object phone;
        private Object wxappid;
        private Object emai;
        private int id;
        private String refresh_token;

        public StatusBean getStatus() {
            return status;
        }

        public void setStatus(StatusBean status) {
            this.status = status;
        }

        public String getScopes() {
            return scopes;
        }

        public void setScopes(String scopes) {
            this.scopes = scopes;
        }

        public long getUuid() {
            return uuid;
        }

        public void setUuid(long uuid) {
            this.uuid = uuid;
        }

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public Object getTel_code() {
            return tel_code;
        }

        public void setTel_code(Object tel_code) {
            this.tel_code = tel_code;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }

        public NewStatusBean getNew_status() {
            return new_status;
        }

        public void setNew_status(NewStatusBean new_status) {
            this.new_status = new_status;
        }

        public Object getInvite_code() {
            return invite_code;
        }

        public void setInvite_code(Object invite_code) {
            this.invite_code = invite_code;
        }

        public String getToken_type() {
            return token_type;
        }

        public void setToken_type(String token_type) {
            this.token_type = token_type;
        }

        public Object getPhone() {
            return phone;
        }

        public void setPhone(Object phone) {
            this.phone = phone;
        }

        public Object getWxappid() {
            return wxappid;
        }

        public void setWxappid(Object wxappid) {
            this.wxappid = wxappid;
        }

        public Object getEmai() {
            return emai;
        }

        public void setEmai(Object emai) {
            this.emai = emai;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public static class StatusBean {
            /**
             * status : unaccount
             * web_signup : null
             * permission : ["usStockQuoteLv0","hkStockQuoteLv0"]
             * status_code : 0
             * set_trade_password : false
             * step : ["开户","审核","入金","成功"]
             * link : https://www.itiger.com/accounts_continue
             * hk_left_days : 0
             * status_description :
             * portal_text : 马上开户
             * open_version : 0
             * sdk_info : {"btn_text":"马上开户","code":0,"link":"https://www.itiger.com/accounts_continue","step":["开户","审核","入金","成功"]}
             * a_stock_sdk : {"open_account_sdk":false,"trade_sdk":false}
             * sns_status : {"wechat_binding":false,"xiaomi_binding":false,"wxapp_binding":false,"weibo_binding":false,"facebook_binding":false,"twitter_binding":false}
             * btn_text : 马上开户
             * role : guest
             * open_version_ios : 0
             * status_message : 未注册
             */

            private String status;
            private Object web_signup;
            private int status_code;
            private boolean set_trade_password;
            private String link;
            private int hk_left_days;
            private String status_description;
            private String portal_text;
            private int open_version;
            private SdkInfoBean sdk_info;
            private AStockSdkBean a_stock_sdk;
            private SnsStatusBean sns_status;
            private String btn_text;
            private String role;
            private int open_version_ios;
            private String status_message;
            private List<String> permission;
            private List<String> step;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public Object getWeb_signup() {
                return web_signup;
            }

            public void setWeb_signup(Object web_signup) {
                this.web_signup = web_signup;
            }

            public int getStatus_code() {
                return status_code;
            }

            public void setStatus_code(int status_code) {
                this.status_code = status_code;
            }

            public boolean isSet_trade_password() {
                return set_trade_password;
            }

            public void setSet_trade_password(boolean set_trade_password) {
                this.set_trade_password = set_trade_password;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public int getHk_left_days() {
                return hk_left_days;
            }

            public void setHk_left_days(int hk_left_days) {
                this.hk_left_days = hk_left_days;
            }

            public String getStatus_description() {
                return status_description;
            }

            public void setStatus_description(String status_description) {
                this.status_description = status_description;
            }

            public String getPortal_text() {
                return portal_text;
            }

            public void setPortal_text(String portal_text) {
                this.portal_text = portal_text;
            }

            public int getOpen_version() {
                return open_version;
            }

            public void setOpen_version(int open_version) {
                this.open_version = open_version;
            }

            public SdkInfoBean getSdk_info() {
                return sdk_info;
            }

            public void setSdk_info(SdkInfoBean sdk_info) {
                this.sdk_info = sdk_info;
            }

            public AStockSdkBean getA_stock_sdk() {
                return a_stock_sdk;
            }

            public void setA_stock_sdk(AStockSdkBean a_stock_sdk) {
                this.a_stock_sdk = a_stock_sdk;
            }

            public SnsStatusBean getSns_status() {
                return sns_status;
            }

            public void setSns_status(SnsStatusBean sns_status) {
                this.sns_status = sns_status;
            }

            public String getBtn_text() {
                return btn_text;
            }

            public void setBtn_text(String btn_text) {
                this.btn_text = btn_text;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public int getOpen_version_ios() {
                return open_version_ios;
            }

            public void setOpen_version_ios(int open_version_ios) {
                this.open_version_ios = open_version_ios;
            }

            public String getStatus_message() {
                return status_message;
            }

            public void setStatus_message(String status_message) {
                this.status_message = status_message;
            }

            public List<String> getPermission() {
                return permission;
            }

            public void setPermission(List<String> permission) {
                this.permission = permission;
            }

            public List<String> getStep() {
                return step;
            }

            public void setStep(List<String> step) {
                this.step = step;
            }

            public static class SdkInfoBean {
                /**
                 * btn_text : 马上开户
                 * code : 0
                 * link : https://www.itiger.com/accounts_continue
                 * step : ["开户","审核","入金","成功"]
                 */

                private String btn_text;
                private int code;
                private String link;
                private List<String> step;

                public String getBtn_text() {
                    return btn_text;
                }

                public void setBtn_text(String btn_text) {
                    this.btn_text = btn_text;
                }

                public int getCode() {
                    return code;
                }

                public void setCode(int code) {
                    this.code = code;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public List<String> getStep() {
                    return step;
                }

                public void setStep(List<String> step) {
                    this.step = step;
                }
            }

            public static class AStockSdkBean {
                /**
                 * open_account_sdk : false
                 * trade_sdk : false
                 */

                private boolean open_account_sdk;
                private boolean trade_sdk;

                public boolean isOpen_account_sdk() {
                    return open_account_sdk;
                }

                public void setOpen_account_sdk(boolean open_account_sdk) {
                    this.open_account_sdk = open_account_sdk;
                }

                public boolean isTrade_sdk() {
                    return trade_sdk;
                }

                public void setTrade_sdk(boolean trade_sdk) {
                    this.trade_sdk = trade_sdk;
                }
            }

            public static class SnsStatusBean {
                /**
                 * wechat_binding : false
                 * xiaomi_binding : false
                 * wxapp_binding : false
                 * weibo_binding : false
                 * facebook_binding : false
                 * twitter_binding : false
                 */

                private boolean wechat_binding;
                private boolean xiaomi_binding;
                private boolean wxapp_binding;
                private boolean weibo_binding;
                private boolean facebook_binding;
                private boolean twitter_binding;

                public boolean isWechat_binding() {
                    return wechat_binding;
                }

                public void setWechat_binding(boolean wechat_binding) {
                    this.wechat_binding = wechat_binding;
                }

                public boolean isXiaomi_binding() {
                    return xiaomi_binding;
                }

                public void setXiaomi_binding(boolean xiaomi_binding) {
                    this.xiaomi_binding = xiaomi_binding;
                }

                public boolean isWxapp_binding() {
                    return wxapp_binding;
                }

                public void setWxapp_binding(boolean wxapp_binding) {
                    this.wxapp_binding = wxapp_binding;
                }

                public boolean isWeibo_binding() {
                    return weibo_binding;
                }

                public void setWeibo_binding(boolean weibo_binding) {
                    this.weibo_binding = weibo_binding;
                }

                public boolean isFacebook_binding() {
                    return facebook_binding;
                }

                public void setFacebook_binding(boolean facebook_binding) {
                    this.facebook_binding = facebook_binding;
                }

                public boolean isTwitter_binding() {
                    return twitter_binding;
                }

                public void setTwitter_binding(boolean twitter_binding) {
                    this.twitter_binding = twitter_binding;
                }
            }
        }

        public static class NewStatusBean {
            /**
             * status : unaccount
             * web_signup : null
             * permission : ["usStockQuoteLv0","hkStockQuoteLv0"]
             * status_code : 0
             * sdk_info : {"btn_text":"马上开户","code":0,"link":"https://www.itiger.com/accounts_continue","step":["开户","审核","入金","成功"]}
             * step : ["开户","审核","入金","成功"]
             * sns_status : {"wechat_binding":false,"xiaomi_binding":false,"wxapp_binding":false,"weibo_binding":false,"facebook_binding":false,"twitter_binding":false}
             * a_stock_sdk : {"open_account_sdk":false,"trade_sdk":false}
             * open_version : 0
             * btn_text : 马上开户
             * link : https://www.itiger.com/accounts_continue
             * hk_left_days : 0
             * open_version_ios : 0
             * role : guest
             * set_trade_password : false
             * portal_text : 马上开户
             */

            private String status;
            private Object web_signup;
            private int status_code;
            private SdkInfoBeanX sdk_info;
            private SnsStatusBeanX sns_status;
            private AStockSdkBeanX a_stock_sdk;
            private int open_version;
            private String btn_text;
            private String link;
            private int hk_left_days;
            private int open_version_ios;
            private String role;
            private boolean set_trade_password;
            private String portal_text;
            private List<String> permission;
            private List<String> step;

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public Object getWeb_signup() {
                return web_signup;
            }

            public void setWeb_signup(Object web_signup) {
                this.web_signup = web_signup;
            }

            public int getStatus_code() {
                return status_code;
            }

            public void setStatus_code(int status_code) {
                this.status_code = status_code;
            }

            public SdkInfoBeanX getSdk_info() {
                return sdk_info;
            }

            public void setSdk_info(SdkInfoBeanX sdk_info) {
                this.sdk_info = sdk_info;
            }

            public SnsStatusBeanX getSns_status() {
                return sns_status;
            }

            public void setSns_status(SnsStatusBeanX sns_status) {
                this.sns_status = sns_status;
            }

            public AStockSdkBeanX getA_stock_sdk() {
                return a_stock_sdk;
            }

            public void setA_stock_sdk(AStockSdkBeanX a_stock_sdk) {
                this.a_stock_sdk = a_stock_sdk;
            }

            public int getOpen_version() {
                return open_version;
            }

            public void setOpen_version(int open_version) {
                this.open_version = open_version;
            }

            public String getBtn_text() {
                return btn_text;
            }

            public void setBtn_text(String btn_text) {
                this.btn_text = btn_text;
            }

            public String getLink() {
                return link;
            }

            public void setLink(String link) {
                this.link = link;
            }

            public int getHk_left_days() {
                return hk_left_days;
            }

            public void setHk_left_days(int hk_left_days) {
                this.hk_left_days = hk_left_days;
            }

            public int getOpen_version_ios() {
                return open_version_ios;
            }

            public void setOpen_version_ios(int open_version_ios) {
                this.open_version_ios = open_version_ios;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }

            public boolean isSet_trade_password() {
                return set_trade_password;
            }

            public void setSet_trade_password(boolean set_trade_password) {
                this.set_trade_password = set_trade_password;
            }

            public String getPortal_text() {
                return portal_text;
            }

            public void setPortal_text(String portal_text) {
                this.portal_text = portal_text;
            }

            public List<String> getPermission() {
                return permission;
            }

            public void setPermission(List<String> permission) {
                this.permission = permission;
            }

            public List<String> getStep() {
                return step;
            }

            public void setStep(List<String> step) {
                this.step = step;
            }

            public static class SdkInfoBeanX {
                /**
                 * btn_text : 马上开户
                 * code : 0
                 * link : https://www.itiger.com/accounts_continue
                 * step : ["开户","审核","入金","成功"]
                 */

                private String btn_text;
                private int code;
                private String link;
                private List<String> step;

                public String getBtn_text() {
                    return btn_text;
                }

                public void setBtn_text(String btn_text) {
                    this.btn_text = btn_text;
                }

                public int getCode() {
                    return code;
                }

                public void setCode(int code) {
                    this.code = code;
                }

                public String getLink() {
                    return link;
                }

                public void setLink(String link) {
                    this.link = link;
                }

                public List<String> getStep() {
                    return step;
                }

                public void setStep(List<String> step) {
                    this.step = step;
                }
            }

            public static class SnsStatusBeanX {
                /**
                 * wechat_binding : false
                 * xiaomi_binding : false
                 * wxapp_binding : false
                 * weibo_binding : false
                 * facebook_binding : false
                 * twitter_binding : false
                 */

                private boolean wechat_binding;
                private boolean xiaomi_binding;
                private boolean wxapp_binding;
                private boolean weibo_binding;
                private boolean facebook_binding;
                private boolean twitter_binding;

                public boolean isWechat_binding() {
                    return wechat_binding;
                }

                public void setWechat_binding(boolean wechat_binding) {
                    this.wechat_binding = wechat_binding;
                }

                public boolean isXiaomi_binding() {
                    return xiaomi_binding;
                }

                public void setXiaomi_binding(boolean xiaomi_binding) {
                    this.xiaomi_binding = xiaomi_binding;
                }

                public boolean isWxapp_binding() {
                    return wxapp_binding;
                }

                public void setWxapp_binding(boolean wxapp_binding) {
                    this.wxapp_binding = wxapp_binding;
                }

                public boolean isWeibo_binding() {
                    return weibo_binding;
                }

                public void setWeibo_binding(boolean weibo_binding) {
                    this.weibo_binding = weibo_binding;
                }

                public boolean isFacebook_binding() {
                    return facebook_binding;
                }

                public void setFacebook_binding(boolean facebook_binding) {
                    this.facebook_binding = facebook_binding;
                }

                public boolean isTwitter_binding() {
                    return twitter_binding;
                }

                public void setTwitter_binding(boolean twitter_binding) {
                    this.twitter_binding = twitter_binding;
                }
            }

            public static class AStockSdkBeanX {
                /**
                 * open_account_sdk : false
                 * trade_sdk : false
                 */

                private boolean open_account_sdk;
                private boolean trade_sdk;

                public boolean isOpen_account_sdk() {
                    return open_account_sdk;
                }

                public void setOpen_account_sdk(boolean open_account_sdk) {
                    this.open_account_sdk = open_account_sdk;
                }

                public boolean isTrade_sdk() {
                    return trade_sdk;
                }

                public void setTrade_sdk(boolean trade_sdk) {
                    this.trade_sdk = trade_sdk;
                }
            }
        }
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public boolean isIs_succ() {
        return is_succ;
    }

    public void setIs_succ(boolean is_succ) {
        this.is_succ = is_succ;
    }
}
