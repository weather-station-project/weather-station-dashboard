using System.Threading.Tasks;
using Moq;
using WeatherStationProject.Dashboard.Data;
using WeatherStationProject.Dashboard.GroundTemperatureService.Data;
using Xunit;

namespace WeatherStationProject.Dashboard.GroundTemperatureService.Tests
{
    public class GroundTemperatureServiceTest
    {
        [Fact]
        public async void When_Getting_LastTemperature_Given_Result_Should_Return_RelatedObject()
        {
            // Arrange
            var measurement = new GroundTemperature {Temperature = 1};
            var repository = new Mock<IRepository<GroundTemperature>>();
            repository.Setup(x => x.GetLastMeasurement()).Returns(Task.FromResult(measurement));
            var service = new GroundTemperatureService.Services.GroundTemperatureService(repository.Object);

            // Act
            var result = await service.GetLastTemperature();

            // Assert
            Assert.Equal(measurement, result);
        }
    }
}