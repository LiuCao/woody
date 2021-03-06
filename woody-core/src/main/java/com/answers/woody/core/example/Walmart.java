package com.answers.woody.core.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.answers.woody.core.model.Inject;
import com.answers.woody.core.model.Model;
import com.answers.woody.core.model.annotation.ComboExtract;
import com.answers.woody.core.model.annotation.ComboExtracts;
import com.answers.woody.core.model.annotation.ExprType;
import com.answers.woody.core.model.annotation.ExtractBy;
import com.answers.woody.core.model.annotation.OP;
import com.answers.woody.core.model.annotation.Setting;
import com.answers.woody.core.parser.AnnotationExtractor;
import com.answers.woody.core.util.StringUtil;

public class Walmart extends Model {

  @Inject
  public String url;

  @Inject
  @ComboExtract(value = {
      @ExtractBy(value = "span.bigPriceText1", type = ExprType.CSS),
      @ExtractBy(value = "span.smallPriceText1", type = ExprType.CSS) }, op = OP.JOIN)
  private String price;

  @Inject
  @ComboExtract(value = {
      @ExtractBy(value = "div#OnlineStat p:not([style~=(?i)display:\\s*none])", type = ExprType.CSS),
      @ExtractBy(value = "div#MP_INSTOCK_STATUS_3[style~=(?)block] span:eq(0)", type = ExprType.CSS),
      @ExtractBy(value = "span.InOnline", type = ExprType.CSS),
      @ExtractBy(value = "tr#MP_SELLER_ROW_10 td.cellBR span.OutOnline", type = ExprType.CSS),
      @ExtractBy(value = "tr#MP_SELLER_ROW_10 td.cellBR div.OutOnline", type = ExprType.CSS) }, op = OP.OR)
  private String status;

  @Inject
  @ExtractBy(value = "//ol[@itemprop='breadcrumb']//li[last()]/a/text()")
  private String category;

  @Inject
  @ExtractBy(value = "//meta[@itemprop='brand']/@content")
  private String manufacturer;

  @Inject
  @ExtractBy(value = "//meta[@itemprop='model']/@content")
  private String mpn;

  @Inject
  @ComboExtract(value = {
      @ExtractBy(value = "//meta[@itemprop='name']/@content", type = ExprType.XPATH),
      @ExtractBy(value = "h1.productTitle", type = ExprType.CSS),
      @ExtractBy(value = "h1.productTitle p span", type = ExprType.CSS) }, op = OP.OR)
  private String productName;

  @Inject
  @ComboExtract(value = {
      @ExtractBy(value = "R3_ITEM\\.setId\\(['\"](\\d+)['\"]\\)", type = ExprType.REGEX),
      @ExtractBy(value = "var\\s+DefaultItem\\s*=\\s*\\{\\s*itemId\\s*:\\s*(\\d+)\\s*,", type = ExprType.REGEX),
      @ExtractBy(value = "//input[@name='product_id']/@value") }, op = OP.OR)
  private String productId;

  @Inject
  @ComboExtracts(value = {
      @ComboExtract(value = {
          @ExtractBy(value = "table.SpecTable", type = ExprType.CSS, setting = @Setting(outerHtml = true)),
          @ExtractBy(value = "Walmart No\\.:</td>\\s*<td.+?>(\\d+)</td>", type = ExprType.REGEX) }, op = OP.AND),
      @ComboExtract(value = { @ExtractBy(value = "(?i)\\{\"key\":\"SKU\",\\s*\"value\":\"(\\d+)\"\\}", type = ExprType.REGEX) }) }, op = OP.OR)
  private String channelSKU;

  @Inject
  @ExtractBy(value = "div#UPC_MESSAGE strong#UPC_CODE", type = ExprType.CSS)
  private String upc;

  @Inject
  @ExtractBy(value = "http://content.webcollage.net/walmart/resources/content-player/v2/content-player.min.js", type = ExprType.TEST)
  private boolean wcPlayer;

  @Inject
  @ComboExtract(value = {
      @ExtractBy(value = "http://content.webcollage.net/walmart/resources/content-player/v2/ppp.min.js", type = ExprType.TEST),
      @ExtractBy(value = "div#wc-aplus", type = ExprType.CSS) }, op = OP.OR)
  private boolean wcEmc;

  @Inject
  public int statueCode;

  @Inject
  public String statusMsg;

  public String html;

  public Walmart() {
    super();
    price = "";
    status = "";
    category = "";
    manufacturer = "";
    mpn = "";
    productName = "";
    productId = "";
    channelSKU = "";
    upc = "";
    statueCode = 0;
    statusMsg = "";
    url = "";
    wcPlayer = false;
    wcEmc = false;
    html = "";
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getMpn() {
    return mpn;
  }

  public void setMpn(String mpn) {
    this.mpn = mpn;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getChannelSKU() {
    return channelSKU;
  }

  public void setChannelSKU(String channelSKU) {
    this.channelSKU = channelSKU;
  }

  public String getUpc() {
    return upc;
  }

  public void setUpc(String upc) {
    this.upc = upc;
  }

  public boolean isWcPlayer() {
    return wcPlayer;
  }

  public void setWcPlayer(boolean wcPlayer) {
    this.wcPlayer = wcPlayer;
  }

  public boolean isWcEmc() {
    return wcEmc;
  }

  public void setWcEmc(boolean wcEmc) {
    this.wcEmc = wcEmc;
  }

  public int getStatueCode() {
    return statueCode;
  }

  public void setStatueCode(int statueCode) {
    this.statueCode = statueCode;
  }

  public String getStatusMsg() {
    return statusMsg;
  }

  public void setStatusMsg(String statusMsg) {
    this.statusMsg = statusMsg;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  @Override
  public boolean isValid() {
    if (StringUtil.isNullOrEmpty(this.productId))
      return false;
    return true;
  }

  public static Walmart newInstance() {
    return new Walmart();
  }

  public static void main(String[] args) throws Exception {
    String url = "http://www.walmart.com/ip/19716785";
    Document doc = Jsoup
        .connect(url)
        .timeout(60000)
        .userAgent(
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0")
        .get();
    String html = doc.html();
    Walmart model = AnnotationExtractor.me().process(html, Walmart.class);
    model.url = url;
    System.out.println(model);
  }
}
