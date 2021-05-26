﻿using System.Threading.Tasks;
using WeatherStationProject.Dashboard.AirParametersService.Data;
using WeatherStationProject.Dashboard.Data;

namespace WeatherStationProject.Dashboard.AirParametersService.Services
{
    public class AirParametersService : IAirParametersService
    {
        private readonly IRepository<AirParameters> _repository;

        public AirParametersService(IRepository<AirParameters> repository)
        {
            _repository = repository;
        }

        public async Task<AirParameters> GetLastAirParameters()
        {
            return await _repository.GetLastMeasurement();
        }
    }
}
