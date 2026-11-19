package dev.civcraftreborn.client;

import dev.civcraftreborn.settlement.CampOverviewResponse;

/** Client display cache. Never used to authorize camp mutations. */
public final class CivCraftOverviewState {
    private static CampOverviewResponse latest=CampOverviewResponse.absent();
    private CivCraftOverviewState(){}
    public static void accept(CampOverviewResponse response){latest=response;}
    public static CampOverviewResponse latest(){return latest;}
    public static String stageLabel(){
        if(!latest.exists())return "Лагерь ещё не основан";
        return latest.stage()==1?"Поселение":"Лагерь";
    }
    public static String resourceLabel(){
        return "Жители: "+latest.residents()+" · Комнаты: "+latest.rooms()
                +" · Кровати: "+latest.beds();
    }
    public static String buildingLabel(){
        return "Еда: "+latest.food()+"/16 · Складов: "+latest.storages()
                +" · Мастерская: "+(latest.workshop()?"да":"нет")
                +" · Ферма: "+(latest.farm()?"да":"нет");
    }
    public static String readinessLabel(){
        if(!latest.exists())return "Начните с основания лагеря";
        if(!latest.complete())return "Территория не полностью загружена";
        if(latest.stage()==1)return "Поселение основано";
        return latest.rooms()>=3&&latest.residents()>=3&&latest.food()>=16
                &&latest.storages()>0&&latest.workshop()&&latest.farm()
                ?"Проверьте условия повышения в меню лагеря"
                :"Продолжайте строительство и развитие лагеря";
    }
}
