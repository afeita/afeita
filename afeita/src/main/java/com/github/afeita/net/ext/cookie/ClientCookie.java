package com.github.afeita.net.ext.cookie;

/**
 * Cookie 信息 相关的属性，包括Cookie版本0与Cookie版本1
 * Set-Cookie: NAME=VALUE; expires=DATE; path=PATH; domain=DOMAIN_NAME; secure  -- Cookie0
 *
 * <br /> author: chenshufei
 * <br /> date: 15/9/13
 * <br /> email: chenshufei2@sina.com
 */
public interface ClientCookie {

    /**
     * 响应的，有可能是一条SetCookie,多条Cookie过来中间有,逗号分割
     * Set-Cookie
     * JSESSIONID=4RqoHAJ3qm3GUopCfWu8umNo.idc-wildfly-new-2; path=/user,
     * route=b147aa6e0c7fbf2378f8ee811c6f0e5f;Domain=.huijiacn.com;Path=/;Max-Age=80000,
     * hj_version=v1;Path=/;Max-Age=31536000
     */



    /**
     * Cookies版本1(RFC2965)支持的属性，必选 (注意，有可能会遇上不专业的服务端开发，设置了max-age用了版本1，且没有给version)
     * 强制的。这个属性值是一个整数，对应于cookie规范的版本、RFC 2965 为版本1：
     * Set-Cookie2: part="Rocket_launcher_0001"; Version="1"
     */
    public static final String VERSION_ATTR    = "version";
    /**
     * 可选的。通过这个属性可以为服务器上特定的文档分配Cookie，
     * 如果path属性是一个URL路径前缀，就可以附加一个Cookie。
     */
    public static final String PATH_ATTR       = "path";
    /**
     * 可选的。浏览器只向指定的服务器主机名发送Cookie。这样服务器就将Cookie限制在了特定的域中。
     * <br />acme.com域与anvil.acme.com 和shipping.crat.acme.com相匹配，但是与www.cnn.com就不匹配。
     */
    public static final String DOMAIN_ATTR     = "domain";
    /**
     * Cookies版本1(RFC2965)，可选
     * 这个属性的值是一个整数，用于设置以秒为单位cookie生存期。
     * 客户端应该根据HTTP/1.1的使用期计算规则来计算cookie的使用期。
     * cookie的使用期比Max-Age大时，客户端就应该将这个cookie丢弃。
     * 值为零说明应该立即将那个cookie丢弃。
     */
    public static final String MAX_AGE_ATTR    = "max-age";
    /**
     * 如果包含这个属性，就只有在HTTP中使用SSL安全连接时才能发送cookie
     * 只有secure一个属性，没有value
     */
    public static final String SECURE_ATTR     = "secure";
    //Cookies版本1(RFC2965)，可选
    public static final String COMMENT_ATTR    = "comment";
    /**
     * 可选的。这个属性会指定一个日期字符串，用来定义Cookie的实际生存期。一旦到了过期日期，就不再存储或发布这个Cookie了
     */
    public static final String EXPIRES_ATTR    = "expires";

    /** Cookies版本1(RFC2965)，可选
     * 可选的。这个属性可以单独作为关键字使用，也可以包含一个由逗号分隔的、可以应用cookie的端口列表。
     * 如果有端口列表， 就只能向端口与列表中的端口相匹配的服务器提供cookie，
     * 如果单独提供关键字Port而没有值，就只能向当前响应服务器的端口号提供cookie
     */
    public static final String PORT_ATTR       = "port";
    //Cookies版本1(RFC2965)，可选
    public static final String COMMENTURL_ATTR = "commenturl";

    /**
     * Cookies版本1(RFC2965)，可选 ，如果提供了这个属性，就会在客户端程序终止时，指示客户端放弃这个cookie
     * discard，可选基本上不用，默认是false,
     * 在应用退出时，调用isPersistent，检查cookie是否应该要去掉，（主要判断是否有expireDate与是否有discard）
     */
    public static final String DISCARD_ATTR    = "discard";

    /**
     * Cookie的所有属性
     */
    public static final String[] ATTRS ={VERSION_ATTR,PATH_ATTR,DOMAIN_ATTR,MAX_AGE_ATTR,
            SECURE_ATTR,COMMENT_ATTR,EXPIRES_ATTR,PORT_ATTR,COMMENTURL_ATTR,DISCARD_ATTR};

    String getAttribute(String name);

    boolean containsAttribute(String name);
}
