using Renci.SshNet;
using System;
using System.Collections.Generic;
using System.Configuration;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Text.RegularExpressions;
using System.Threading.Tasks;

namespace Wmsi.SGX.Operations
{
    public static class Utilities
    {
        static Regex csvSplit = new Regex("(?:^|,)(\"(?:[^\"]+|\"\")*\"|[^,]*)", RegexOptions.Compiled);

        public static string[] SplitCSV(string input)
        {

            List<string> csvItems = new List<string>();
            foreach (Match match in csvSplit.Matches(input))
            {
                csvItems.Add(match.Value.Trim().TrimStart(','));
            }
            return csvItems.ToArray();
        }

        public static DateTime FromUnixTime(long unixTime)
        {
            var epoch = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
            return epoch.AddSeconds((unixTime / 1000));
        }

        public static string[] DownloadTextFile(string url)
        {
            using (var client = new HttpClient())
            {
                //client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(m_AccessToken.Type, m_AccessToken.Value);
                //var gaResponse = await client.GetAsync(request.BuildUri(m_Url));
                //var jObject = JObject.Parse(await gaResponse.Content.ReadAsStringAsync());
                var gaResponse = client.GetAsync(url);
                var response = gaResponse.Result.Content.ReadAsStringAsync();
                return response.Result.Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries);
            }
        }

        public static string[] DownloadFTPTextFile(string fileName)
        {
            string url = ConfigurationManager.AppSettings["ftpURL"].ToString();
            string user = ConfigurationManager.AppSettings["ftpUsername"].ToString();
            string pass = ConfigurationManager.AppSettings["ftpPassword"].ToString();

            using (SftpClient scp = new SftpClient(url, user, pass))
            {
                scp.Connect();
                var memstream = new MemoryStream();
                scp.DownloadFile(fileName, memstream);
                scp.Disconnect();

                memstream.Position = 0; //go back to first position before loading into StreamReader.
                var sr = new StreamReader(memstream);
                return sr.ReadToEnd().Split(new[] { Environment.NewLine }, StringSplitOptions.RemoveEmptyEntries);
            }
        }
    }
}
