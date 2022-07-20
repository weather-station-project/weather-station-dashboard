using System.Collections.Generic;
using System.Linq;
using System.Text.Json.Serialization;
using WeatherStationProject.Dashboard.AirParametersService.Data;
using WeatherStationProject.Dashboard.Data.Validations;
using WeatherStationProject.Dashboard.Data.ViewModel;

namespace WeatherStationProject.Dashboard.AirParametersService.ViewModel
{
    public sealed class HistoricalDataDTO : GroupedDTO<AirParameters>
    {
        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
        public Dictionary<string, SummaryDTO> SummaryByGroupingItem { get; }
        
        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)]
        public List<AirParametersDTO> Measurements { get; }

        public HistoricalDataDTO (
            List<AirParameters> entities,
            GroupingValues grouping,
            bool includeSummary,
            bool includeMeasurements)
        {
            if (includeSummary)
            {
                SummaryByGroupingItem = new Dictionary<string, SummaryDTO>();
            }

            if (includeMeasurements)
            {
                Measurements = new List<AirParametersDTO>();
            }
            
            var groupedEntities = GroupEntities(entities,
                grouping,
                includeSummary,
                includeMeasurements);

            PopulateGroupedSummaries(groupedEntities);
        }
        
        protected override Dictionary<string, List<AirParameters>> GroupEntities(List<AirParameters> entities,
            GroupingValues grouping,
            bool includeSummary,
            bool includeMeasurements)
        {
            var groupedEntities = new Dictionary<string, List<AirParameters>>();

            foreach (var entity in entities)
            {
                if (includeMeasurements) Measurements.Add(AirParametersDTO.FromEntity(entity));

                if (!includeSummary) continue;

                var key = GetGroupingKey(entity, grouping);
                if (groupedEntities.ContainsKey(key))
                    groupedEntities[key].Add(entity);
                else
                    groupedEntities.Add(key, new List<AirParameters> {entity});
            }

            return groupedEntities;
        }

        protected override void PopulateGroupedSummaries(Dictionary<string, List<AirParameters>> groupedEntities)
        {
            foreach (var (key, value) in groupedEntities)
            {
                var pressureAvg = value.Average(x => x.Pressure);
                var humidityAvg = value.Average(x => x.Humidity);
                
                SummaryByGroupingItem.Add(key, new SummaryDTO
                {
                    HumidityAvg = humidityAvg,
                    PressureAvg = pressureAvg
                });
            }
        }
    }
}