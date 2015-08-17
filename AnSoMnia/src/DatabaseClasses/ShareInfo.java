package DatabaseClasses;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ShareInfo {

	@Id
	protected String isin;
	
	protected String name;
	
	protected String ticker;
	
	protected String wkn;
		
	protected String valor;
	
	protected String wallstreet_query_string;
	
	protected String finance_query_string;
	
	public String getFinanceQueryString() {
		return finance_query_string;
	}

	public void setFinanceQueryString(String finance_query_string) {
		this.finance_query_string = finance_query_string;
	}
	
	public String getIsin() {
		return isin;
	}
	public void setIsin(String isin) {
		this.isin = isin;
	}
	public String getName() {
		return name;
	}
	public void setName(String company_name) {
		this.name = company_name;
	}
	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public String getWallstreetQueryString() {
		return wallstreet_query_string;
	}

	public void setWallstreetQueryString(String wallstreet_query_string) {
		this.wallstreet_query_string = wallstreet_query_string;
	}
	
	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
	
}
