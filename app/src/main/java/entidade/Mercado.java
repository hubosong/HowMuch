package entidade;

import java.io.Serializable;

public class Mercado implements Serializable {

    private long id_mercado;
    private String nome;
    private String nome_fantasia;
    private String cnpj;
    private String endereco;
    private String bairro;
    private String cep;
    private String municipio;
    private String telefone;
    private String uf;
    private String pais;
    private String inscricao_estadual;
    private String cod_regime_tributario;
    private String horarios_funcionamento;

    @Override
    public String toString() {
        return "Mercado{" + "id_mercado=" + id_mercado + ", nome=" + nome + ", nome_fantasia=" + nome_fantasia + ", cnpj=" + cnpj + ", endereco=" + endereco + ", bairro=" + bairro + ", cep=" + cep + ", municipio=" + municipio + ", telefone=" + telefone + ", uf=" + uf + ", pais=" + pais + ", inscricao_estadual=" + inscricao_estadual + ", cod_regime_tributario=" + cod_regime_tributario + '}';
    }

    public String getHorarios_funcionamento() {
        return horarios_funcionamento;
    }

    public void setHorarios_funcionamento(String horarios_funcionamento) {
        this.horarios_funcionamento = horarios_funcionamento;
    }

    public long getId_mercado() {
        return id_mercado;
    }

    public void setId_mercado(long id_mercado) {
        this.id_mercado = id_mercado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome_fantasia() {
        return nome_fantasia;
    }

    public void setNome_fantasia(String nome_fantasia) {
        this.nome_fantasia = nome_fantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getInscricao_estadual() {
        return inscricao_estadual;
    }

    public void setInscricao_estadual(String inscricao_estadual) {
        this.inscricao_estadual = inscricao_estadual;
    }

    public String getCod_regime_tributario() {
        return cod_regime_tributario;
    }

    public void setCod_regime_tributario(String cod_regime_tributario) {
        this.cod_regime_tributario = cod_regime_tributario;
    }

}
