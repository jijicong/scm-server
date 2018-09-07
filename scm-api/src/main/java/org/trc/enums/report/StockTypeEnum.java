package org.trc.enums.report;


public enum StockTypeEnum {
    QUALITY("1", "正品"),
    SUBSTANDARD("2", "残品");

    public static StockTypeEnum queryNameByCode(String code){
        for(StockTypeEnum stockTypeEnum: StockTypeEnum.values()){
            if (stockTypeEnum.getCode().equals(code)){
                return stockTypeEnum;
            }
        }
        return null;
    }

    private String code;
    private String name;

    StockTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
