package br.inpe.dpi.terrabrasilis.geoserveranalyzer.model;

public class BusinessAPILayer 
{
    private String workspace;
    private String name;

    public String getWorkspace() {
        return this.workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return "{" +
            " workspace='" + getWorkspace() + "'" +
            ", name='" + getName() + "'" +
            "}";
    }

}
