package br.com.senac.appcar.modelo;

public class AppCar {
    private int id;
    private String carro;
    private String placa;
    private String servico;

    public AppCar(int id, String carro, String placa, String servico) {
        this.id = id;
        this.carro = carro;
        this.placa = placa;
        this.servico = servico;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCarro() {
        return carro;
    }

    public void setCarro(String carro) {
        this.carro = carro;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getServico() {
        return servico;
    }

    public void setServico(String servico) {
        this.servico = servico;
    }
}
