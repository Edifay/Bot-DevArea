package devarea.bot.commands.object_for_stock;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import devarea.bot.automatical.MessageSeria;

public class Mission {

    @JsonProperty("title")
    protected String title;
    @JsonProperty("description")
    protected String descriptionText;
    @JsonProperty("prix")
    protected String prix;
    @JsonProperty("dateRetour")
    protected String dateRetour;
    @JsonProperty("langage")
    protected String langage;
    @JsonProperty("support")
    protected String support;
    @JsonProperty("niveau")
    protected String niveau;

    @JsonProperty("message")
    protected MessageSeria message;
    @JsonProperty("membre")
    protected String memberId;

    public Mission() {

    }

    public Mission(final String title, final String descriptionText, final String prix, final String dateRetour, final String langage, final String support, final String niveau, final String memberId, final MessageSeria message) {
        this.title = title;
        this.descriptionText = descriptionText;
        this.prix = prix;
        this.dateRetour = dateRetour;
        this.langage = langage;
        this.support = support;
        this.niveau = niveau;
        this.message = message;
        this.memberId = memberId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonIgnore
    public String getTitle() {
        return this.title;
    }

    public void setDescriptionText(String description) {
        this.descriptionText = description;
    }

    @JsonIgnore
    public String getDescriptionText() {
        return this.descriptionText;
    }

    public void setPrix(String prix) {
        this.prix = prix;
    }

    @JsonIgnore
    public String getPrix() {
        return this.prix;
    }

    public void setDateRetour(String date) {
        this.dateRetour = date;
    }

    @JsonIgnore
    public String getDateRetour() {
        return this.dateRetour;
    }

    public void setLangage(String langage) {
        this.langage = langage;
    }

    @JsonIgnore
    public String getLangage() {
        return this.langage;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    @JsonIgnore
    public String getSupport() {
        return this.support;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    @JsonIgnore
    public String getNiveau() {
        return this.niveau;
    }

    public void setMessage(MessageSeria message) {
        this.message = message;
    }

    @JsonIgnore
    public MessageSeria getMessage() {
        return this.message;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    @JsonIgnore
    public String getMemberId() {
        return this.memberId;
    }

    @JsonIgnore
    @Override
    public String toString() {
        return "Mission{" +
                "title='" + title + '\'' +
                ", descriptionText='" + descriptionText + '\'' +
                ", prix='" + prix + '\'' +
                ", dateRetour='" + dateRetour + '\'' +
                ", langage='" + langage + '\'' +
                ", support='" + support + '\'' +
                ", niveau='" + niveau + '\'' +
                ", message=" + message +
                ", memberId='" + memberId + '\'' +
                '}';
    }
}