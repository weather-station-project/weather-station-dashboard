﻿using Microsoft.EntityFrameworkCore;
using WeatherStationProject.Dashboard.Core.Configuration;
using WeatherStationProject.Dashboard.Data;

namespace WeatherStationProject.Dashboard.GroundTemperatureService.Data
{
    public class GroundTemperaturesDbContext : WeatherStationDatabaseContext
    {
        private readonly IAppConfiguration _appConfiguration;

        public GroundTemperaturesDbContext(IAppConfiguration appConfiguration)
        {
            _appConfiguration = appConfiguration;
        }

        public virtual DbSet<GroundTemperature> GroundTemperatures { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseNpgsql(connectionString: _appConfiguration.DatabaseConnectionString);
            base.OnConfiguring(optionsBuilder);
        }
    }
}
