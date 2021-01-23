package edu.harvard.iq.dataverse;

public class DoiSuffixTemplate {
    public String template;
    public int count;

    public DoiSuffixTemplate copy() {
        DoiSuffixTemplate doiSuffixTemplate = new DoiSuffixTemplate();
        doiSuffixTemplate.template = this.template;
        doiSuffixTemplate.count = 1;
        return doiSuffixTemplate;
    }
}
