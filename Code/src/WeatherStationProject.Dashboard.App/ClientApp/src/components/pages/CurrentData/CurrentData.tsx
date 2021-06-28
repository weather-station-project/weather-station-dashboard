import React from "react";
import { useTranslation } from "react-i18next";
import LastDataFromSensors from "./LastDataFromSensors";
import ForecastData from "./ForecastData";
import { ErrorBoundary } from "../../../ErrorBoundary";


const CurrentData: React.FC = () => {
    const { t } = useTranslation();

    return (
        <div style={{ paddingTop: 20 }}>
            <h1>{t("current_data.last_data_title")}</h1>
            <ErrorBoundary>
                <LastDataFromSensors weatherApiHost="https://localhost:44375" authServiceHost="https://localhost:44394" secret="123456" />
            </ErrorBoundary>
            <br />
            <h1>{t("current_data.forecast_title")}</h1>
            <ErrorBoundary>
                <ForecastData />
            </ErrorBoundary>
        </div>
    );
}

export default CurrentData;